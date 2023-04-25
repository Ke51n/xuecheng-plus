package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author Mr.M
     * @date 2022/9/10 8:57
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件，同时保存文件元信息
     * 上传文件需要机构id，上传的数据元信息（用于写到媒资数据库），本地路径（用于文件上传到MinIO）
     *
     * @param companyId
     * @param uploadFileParamsDto
     * @param localFilePath
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);

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
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

    /**
     * 根据md5值检查minio中是否有该文件
     * 根据minio的目录结构约定，只需检查是否有/video/第一位/第二位/md5目录即可
     *
     * @param fileMd5
     * @return
     */
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 分块文件上传前的检测 检查该块是否存在，用于断点续传或多线程
     *
     * @param fileMd5
     * @param chunk
     * @return
     */
    RestResponse<Boolean> checkChunk(String fileMd5, int chunk);

    /**
     * 分块文件上传前的检测 检查该块是否存在，用于断点续传或多线程
     *
     * @param fileMd5
     * @param chunk
     * @return
     */
    RestResponse<Boolean> checkChunk1(String fileMd5, int chunk);

    /**
     * 上传分块
     * @param fileMd5 总文件md5
     * @param chunk 分块号
     * @param localChunkFilePath 本地分块路径
     * @return
     */
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath);

    /**
     * 合并分块
     * @param companyId
     * @param fileMd5
     * @param chunkTotal 分块数
     * @param uploadFileParamsDto  上传普通文件后保存文件信息dto
     * @return
     */
    public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);
}
