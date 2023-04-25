package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MinioClient minioClient;
    @Autowired
    MediaFileService currentProxy;

    //存储普通文件
    @Value("${minio.bucket.files}")
    private String bucket_mediafiles;

    //存储视频
    @Value("${minio.bucket.videofiles}")
    private String bucket_videofiles;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    /**
     * 上传文件，同时保存文件元信息
     * 上传文件需要机构id，上传的数据元信息（用于写到媒资数据库），本地路径（用于文件上传到MinIO）
     *
     * @param companyId
     * @param uploadFileParamsDto
     * @param localFilePath
     * @return
     */
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        //1.文件上传到minio
        //1.1文件名
        String filename = uploadFileParamsDto.getFilename();
        //1.2先得到扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //1.3得到mimetype
        String mimeType = getMimeType(extension);

        //子目录
        String defaultFolderPath = getDefaultFolderPath();
        //文件的md5值
        String fileMd5 = getFileMd5(new File(localFilePath));
        String objectName = defaultFolderPath + fileMd5 + extension;
        //上传文件到minio
        boolean result = this.uploadMediaFilesToMinIO(localFilePath, mimeType, bucket_mediafiles, objectName);
        if (!result) {
            XueChengPlusException.cast("上传文件失败");
        }
        //入库文件信息,这里通过代理对象调用该方法，保证 addMediaFilesToDb 方法受事务控制 todo
        //通过CGLIB代理，实现该方法受事务控制
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_mediafiles, objectName);
        if (mediaFiles == null) {
            XueChengPlusException.cast("文件上传后保存信息失败");
        }
        //准备返回的对象
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    /**
     * 保存媒体文件元信息到数据库
     *
     * @param companyId
     * @param fileMd5
     * @param uploadFileParamsDto
     * @param bucket
     * @param objectName
     * @return
     */
    @Override
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        //将文件信息保存到数据库
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            //文件id
            mediaFiles.setId(fileMd5);
            //机构id
            mediaFiles.setCompanyId(companyId);
            //桶
            mediaFiles.setBucket(bucket);
            //file_path
            mediaFiles.setFilePath(objectName);
            //file_id
            mediaFiles.setFileId(fileMd5);
            //url
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            //上传时间
            mediaFiles.setCreateDate(LocalDateTime.now());
            //状态
            mediaFiles.setStatus("1");
            //审核状态
            mediaFiles.setAuditStatus("002003");
            //插入数据库
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert <= 0) {
                log.debug("向数据库保存文件失败,bucket:{},objectName:{}", bucket, objectName);
                return null;
            }
            return mediaFiles;
        }
        return mediaFiles;
    }

    /**
     * 根据md5值检查minio中是否有该文件
     * 根据minio的目录结构约定，只需检查是否有/video/第一位/第二位/md5目录即可
     * 先从数据库查询该文件的bucket和路径，然后去minio中查询,也即是这里认为若数据库没有，则minio也没有，实际不合理
     *
     * @param fileMd5
     * @return
     */
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //查询文件信息
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //桶
            String bucket = mediaFiles.getBucket();
            //存储目录
            String filePath = mediaFiles.getFilePath();
            //文件流
            InputStream stream = null;
            try {
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build());

                if (stream != null) {
                    //文件已存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                log.error("读取MinIO异常");
            }
        }
        //文件不存在
        return RestResponse.success(false);
    }

    /**
     * 分块文件上传前的检测 检查该块是否存在，用于断点续传或多线程
     *
     * @param fileMd5
     * @param chunk
     * @return
     */
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunk) {
        //查询文件信息
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //桶
            String bucket = mediaFiles.getBucket();
            //存储目录,只是路径
            String filePath = mediaFiles.getFilePath();
            //这里注意分割字符串时.是特殊字符，要转义
            String fp = filePath.split("\\.")[0];
            //文件流
            InputStream stream = null;
            try {
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                //判断分开是否存在
                                .object(fp + "chunk" + chunk)
                                .build());

                if (stream != null) {
                    //分块已存在
                    return RestResponse.success(true);
                } else {
                    //分块不存在
                    return RestResponse.success(false);
                }
            } catch (Exception e) {
                log.error("读取MinIO异常");
            }
        }
        //数据库中记录都不存在，分块更不存在，这里有一致性问题，先认为minio中若有数据，则数据库中一定有记录，待优化 todo
        return RestResponse.success(false);
    }

    /**
     * 获取文件默认存储目录路径 年/月/日
     *
     * @return
     */
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/") + "/";
        return folder;
    }

    /**
     * 获取文件的md5
     *
     * @param file
     * @return
     */
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据扩展名获取mimetype
     *
     * @param extention 扩展名
     * @return
     */
    public String getMimeType(String extention) {
        if (extention == null) {
            //防止抛null pointer 异常
            extention = "";
        }
        //根据扩展名获取mimetype
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extention);
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    /**
     * 将文件上传到minio
     *
     * @param localFilePath 本地文件路径
     * @param mimeType      mime类型
     * @param bucket        上传到哪个桶
     * @param objectName    上传到哪个路径
     */
    public boolean uploadMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {

        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket(bucket)//上传到哪个桶
                    .object(objectName)//上传后的文件路径和名称
                    .filename(localFilePath)//本地文件路径
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(testbucket);//上传
            log.debug("上传文件到minio成功,bucket:{},objectName:{},错误信息:{}", bucket, objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件出错,bucket:{},objectName:{},错误信息:{}", bucket, objectName, e.getMessage());
            XueChengPlusException.cast("上传文件到文件系统失败");
            return false;
        }
    }

    /**
     * 分块文件上传前的检测 检查该块是否存在，用于断点续传或多线程
     *
     * @param fileMd5
     * @param chunkIndex
     * @return
     */

    @Override
    public RestResponse<Boolean> checkChunk1(String fileMd5, int chunkIndex) {

        //得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;

        //文件流
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket_videofiles)//这里是大文件才需要分块，默认大文件都是视频，其实不然，待优化 todo
                            .object(chunkFilePath)
                            .build());

            if (fileInputStream != null) {
                //分块已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {

        }
        //分块未存在
        return RestResponse.success(false);
    }

    /**
     * 上传分块文件
     *
     * @param fileMd5            总文件md5
     * @param chunk              分块号
     * @param localChunkFilePath 本地分块路径
     * @return
     */
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {
        String chunkPath = getChunkFileFolderPath(fileMd5) + chunk;
        String mimeType = getMimeType(null);
        //上传分块文件到minio
        boolean b = uploadMediaFilesToMinIO(localChunkFilePath, mimeType, bucket_videofiles, chunkPath);
        if (b) {
            log.debug("上传分块文件成功:{}", chunkPath);
            return RestResponse.success("true");
        } else {
            log.debug("上传分块文件失败:{}", chunkPath);
            return RestResponse.validfail(false, String.format("分块 %s 上传失败", chunkPath));
        }
    }

    /**
     * 合并分块
     *
     * @param companyId
     * @param fileMd5
     * @param chunkTotal          分块数
     * @param uploadFileParamsDto 上传普通文件后保存文件信息dto
     * @return
     */
    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //=====获取分块文件路径=====
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //组成将分块文件路径组成 List<ComposeSource>
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket_videofiles)//分块文件的桶
                        .object(chunkFileFolderPath.concat(Integer.toString(i)))//minio中的分块文件全路径
                        .build())
                .collect(Collectors.toList());
        //=====合并=====
        //文件名称
        String fileName = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        //合并文件路径 /桶/第一位/第二位/md5/md5.后缀
        String mergeFilePath = getFilePathByMd5(fileMd5, extName);
        try {
            //合并文件
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucket_videofiles)
                            .object(mergeFilePath)
                            .sources(sourceObjectList)
                            .build());
            log.debug("合并文件成功:{}", mergeFilePath);
        } catch (Exception e) {
            log.debug("合并文件失败,fileMd5:{},异常:{}", fileMd5, e.getMessage(), e);
            return RestResponse.validfail(false, "合并文件失败。");
        }
        // ====验证md5====
        //下载合并后的文件==>优化直接从minio获取md5值，节约贷带宽
        //参考：https://www.hxstrive.com/subject/minio/682.htm
//        File minioFile = downloadFileFromMinIO(bucket_videofiles, mergeFilePath);
        try {
            //优化：从minio获取文件的md5值
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucket_videofiles)
                    .object(mergeFilePath)
                    .build());
            long size = statObjectResponse.size();
            String object = statObjectResponse.object();
            String md5 = object.substring(object.lastIndexOf('/') + 1, object.lastIndexOf('.'));
            if (!md5.equals(fileMd5)) {
                return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
            }
            //文件大小
            uploadFileParamsDto.setFileSize(size);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("从minio获取文件元信息失败,fileMd5:{},异常:{}", fileMd5, e.getMessage(), e);
        }
       /* if (minioFile == null) {
            log.debug("下载合并后文件失败,mergeFilePath:{}", mergeFilePath);
            return RestResponse.validfail(false, "下载合并后文件失败。");
        }
        try (InputStream newFileInputStream = new FileInputStream(minioFile)) {
            //minio上文件的md5值
            String md5Hex = DigestUtils.md5Hex(newFileInputStream);
            //比较md5值，不一致则说明文件不完整
            if (!fileMd5.equals(md5Hex)) {
                return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
            }
            //文件大小
            uploadFileParamsDto.setFileSize(minioFile.length());
        } catch (Exception e) {
            log.debug("校验文件失败,fileMd5:{},异常:{}", fileMd5, e.getMessage(), e);
            return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
        } finally {
            if (minioFile != null) {
                minioFile.delete();
            }
        }*/
        //=====文件入库=====
        currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videofiles, mergeFilePath);
        //=====清除分块文件=====
        clearChunkFiles(chunkFileFolderPath, chunkTotal);
        return RestResponse.success(true);
    }

    /**
     * 清除minio上的分块文件
     *
     * @param chunkFileFolderPath
     * @param chunkTotal
     */
    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal) {
        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("video").objects(deleteObjects).build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            //遍历一次才真正删除
            results.forEach(r -> {
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清除分块文件失败,objectname:{}", deleteError.objectName(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清除分块文件失败,chunkFileFolderPath:{}", chunkFileFolderPath, e);
        }
        log.error("清除分块文件成功,chunkFileFolderPath:{}", chunkFileFolderPath);
    }

    /**
     * 从minio下载文件
     *
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    public File downloadFileFromMinIO(String bucket, String objectName) {
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //创建临时文件
            minioFile = File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 得到合并后的文件的地址
     *
     * @param fileMd5 文件id即md5值
     * @param fileExt 文件扩展名
     * @return
     */
    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

    /**
     * 得到分块文件的目录，根据md5值
     *
     * @param fileMd5
     * @return
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

}
