package com.xuecheng;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;


/**
 * package:  com.xuecheng
 * project_name:  xuecheng-plus
 * 2023/4/21  14:01
 * description:测试文件系统 MinIO 使用 java SDK 实现文件上传/查询/删除
 *
 * @author wk
 * @version 1.0
 */
public class MinioTest {
    //根据扩展名取出mimeType

    String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
    //1.创建minio客户端，用于与服务端通信
    MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.101.65:9000")
            .credentials("minioadmin", "minioadmin")
            .build();

    /**
     * 测试上传文件
     *
     * @throws Exception
     */
    @Test
    void testUpload() throws Exception {
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".jpeg");
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        System.out.println(String.format("mimeType=%s", mimeType));

        boolean b = minioClient.bucketExists(
                BucketExistsArgs
                        .builder()
                        .bucket("testbucket")
                        .build()
        );
        if (!b) {
            //创建新桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("testbucket").build());
            System.out.println(String.format("%s桶创建成功", "testbucket"));
        } else {
            System.out.println(String.format("%s桶已经存在", "testbucket"));
        }
        //2.上传文件
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")//上传到哪个桶
                    .object("/test/pic/rb3.png")//上传后的文件路径和名称
                    .filename("C:\\Users\\tax\\Desktop\\文件\\Rainbow.JPEG")//本地文件路径
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(testbucket);//上传
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }
    }

    /**
     * 测试删除文件
     */
    @Test
    void testDel() {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("/test/pic/rb2.png")
                .build();
        //1.删除文件
        try {
            minioClient.removeObject(removeObjectArgs);
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败");
        }
    }

    /**
     * 测试获取文件
     */
    @Test
    void testGet() {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("/test/pic/rb.png")
                .build();
        try {
            InputStream stream = minioClient.getObject(getObjectArgs);
            FileOutputStream outputStream = new FileOutputStream("C:\\Users\\tax\\Desktop\\文件\\rrr.png");
            IOUtils.copy(stream, outputStream);
            //校验文件的完整性对文件的内容进行md5
            FileInputStream fileInputStream1 = new FileInputStream("C:\\Users\\tax\\Desktop\\文件\\rrr.png");
            String source_md5 = DigestUtils.md5Hex(fileInputStream1);
            FileInputStream fileInputStream2 = new FileInputStream("C:\\Users\\tax\\Desktop\\文件\\Rainbow.JPEG");
            String local_md5 = DigestUtils.md5Hex(fileInputStream2);
            if (source_md5.equals(local_md5)) {
                System.out.println("下载成功并校验通过");
            }
        } catch (Exception e) {
            System.out.println("获取失败");
        }
    }

}
