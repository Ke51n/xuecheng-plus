package com.xuecheng;

import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * package:  com.xuecheng
 * project_name:  xuecheng-plus
 * 2023/4/21  22:23
 * description:测试大文件 断点续传
 *
 * @author wk
 * @version 1.0
 */
public class BigFileTest {


    MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.101.65:9000")
            .credentials("minioadmin", "minioadmin")
            .build();

    /**
     * 测试大文件分块方法
     *
     * @throws IOException
     */
    @Test
    public void testChunk() throws IOException {
        File sourceFile = new File("D:\\招标3小组开会.mp4");
        String chunkPath = "D:develop\\bigfile_test\\招标3小组开会\\chunk\\";
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        //分块大小 1MB-》5MB
        long chunkSize = 1024 * 1024 * 5;
        //分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        System.out.println(String.format("总分块数为%s", chunkNum));
        //缓冲区大小
        byte[] b = new byte[1024];
        //使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        //分块
        for (int i = 0; i < chunkNum; i++) {
            //创建分块文件
            File file = new File(chunkPath + i);
            if (file.exists()) {
                file.delete();
            }
            boolean newFile = file.createNewFile();
            if (newFile) {
                //向分块文件中写数据
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }
                raf_write.close();
                System.out.println("完成分块" + i);
            }
        }
        raf_read.close();
    }

    /**
     * 测试分块组合成大文件方法
     *
     * @throws IOException
     */
    @Test
    public void testChunkMerge() throws IOException {
        String chunkPath = "D:develop\\bigfile_test\\招标3小组开会\\chunk\\";
        File chunkFolder = new File(chunkPath);
        int chunkNum = chunkFolder.listFiles().length;
        String newFilePath = "D:develop\\bigfile_test\\bigFiles\\";
        File filePath = new File(newFilePath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        RandomAccessFile newFile = new RandomAccessFile(new File(newFilePath + "招标3小组开会.mp4"), "rw");
        byte[] bytes = new byte[1024];
        for (int i = 0; i < chunkNum; i++) {
            RandomAccessFile curChunk = new RandomAccessFile(new File(chunkPath + i), "r");
            int len = -1;
            //当前分块写入文件
            while ((len = curChunk.read(bytes)) != -1) {
                newFile.write(bytes, 0, len);
            }
            curChunk.close();
        }
        newFile.close();
        System.out.println(String.format("文件合并成功,前往%s查看", newFilePath));
    }

    /**
     * 测试分块合并成文件并校验
     *
     * @throws IOException
     */
    @Test
    public void testMerge() throws IOException {
        //块文件目录
        File chunkFolder = new File("D:develop\\bigfile_test\\招标3小组开会\\chunk\\");
        //原始文件
        File originalFile = new File("D:\\招标3小组开会.mp4");
        //合并文件
        File mergeFile = new File("D:\\develop\\bigfile_test\\bigFiles\\招标3小组开会.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] b = new byte[1024];
        //分块列表
        File[] fileArray = chunkFolder.listFiles();
        // 转成集合，便于排序
        List<File> fileList = Arrays.asList(fileArray);
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        //合并文件
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);

            }
            raf_read.close();
        }
        raf_write.close();

        //校验文件
        try (
                FileInputStream fileInputStream = new FileInputStream(originalFile);
                FileInputStream mergeFileStream = new FileInputStream(mergeFile);

        ) {
            //取出原始文件的md5
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            //取出合并文件的md5进行比较
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
                System.out.println(mergeFileMd5);
            } else {
                System.out.println("合并文件失败");
            }
        }
    }

    /**
     * 将分块文件上传至minio
     */
    @Test
    public void uploadChunk() {
        String chunkFolderPath = "D:develop\\bigfile_test\\chunk\\";
        File chunkFolder = new File(chunkFolderPath);
        //分块文件
        File[] files = chunkFolder.listFiles();
        //将分块文件上传至minio
        for (int i = 0; i < files.length; i++) {
            try {
                UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                        .bucket("testbucket")
                        .object("chunk/" + i)
                        .filename(files[i].getAbsolutePath())
                        .build();
                minioClient.uploadObject(uploadObjectArgs);
                System.out.println("上传分块成功" + i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试MinIO合并文件，要求分块文件最小5M
     *
     * @throws Exception
     */
    @Test
    public void test_merge() throws Exception {
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i)
                .limit(3)
                .map(i -> ComposeSource.builder()
                        .bucket("testbucket")
                        .object("chunk/".concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge01.mp4")
                .sources(sources)
                .build();
        minioClient.composeObject(composeObjectArgs);

    }

    //清除分块文件
    @Test
    public void test_removeObjects() {
        //合并分块完成将分块文件清除
        List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                .limit(4)
                .map(i -> new DeleteObject("chunk/".concat(Integer.toString(i))))
                .collect(Collectors.toList());

        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("testbucket").objects(deleteObjects).build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(r -> {
            DeleteError deleteError = null;
            try {
                deleteError = r.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
