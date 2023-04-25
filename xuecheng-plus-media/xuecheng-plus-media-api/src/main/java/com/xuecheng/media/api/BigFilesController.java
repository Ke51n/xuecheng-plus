package com.xuecheng.media.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * package:  com.xuecheng.media.api
 * project_name:  xuecheng-plus
 * 2023/4/22  11:07
 * description: 大文件分块管理接口
 *
 * @author wk
 * @version 1.0
 */

@RestController
@Api(value = "大文件分块管理接口", tags = "大文件分块管理接口")
public class BigFilesController {
    @Autowired
    MediaFileService mediaFileService;

    /**
     * 文件上传前检查文件是否存在 检查文件是否存在
     *
     * @param fileMd5
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5) throws Exception {
        return mediaFileService.checkFile(fileMd5);
    }

    /**
     * 分块文件上传前的检测 检查该块是否存在，用于断点续传或多线程
     *
     * @param fileMd5
     * @param chunk
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.checkChunk1(fileMd5, chunk);
    }

    /**
     * 上传分块文件到minio
     *
     * @param file
     * @param fileMd5
     * @param chunk
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {

        //创建临时文件，系统会自动清除
        File tempFile = File.createTempFile("minio", "temp");
        //file是前端传来的分块文件，先将分块文件拷贝到临时文件，然后上传
        file.transferTo(tempFile);
        //分块文件的路径
        String absolutePath = tempFile.getAbsolutePath();
        return mediaFileService.uploadChunk(fileMd5, chunk, absolutePath);
    }

    /**
     * 合并MINIO中的分块文件，然后删除分块文件
     *
     * @param fileMd5
     * @param fileName
     * @param chunkTotal
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(fileName);
        return mediaFileService.mergechunks(companyId, fileMd5, chunkTotal, uploadFileParamsDto);
    }
}
