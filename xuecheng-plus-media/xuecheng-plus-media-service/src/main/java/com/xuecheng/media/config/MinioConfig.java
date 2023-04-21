package com.xuecheng.media.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
/**
 * package:  com.xuecheng.media.config
 * project_name:  xuecheng-plus
 * 2023/4/21  14:50
 * description:
 *
 * @author wk
 * @version 1.0
 */
@Slf4j
@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")
    private String endpoint; //从nacos配置中心读取数据赋值
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        //注入bean到IOC，使用时直接@autowired
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
        return minioClient;
    }
}
