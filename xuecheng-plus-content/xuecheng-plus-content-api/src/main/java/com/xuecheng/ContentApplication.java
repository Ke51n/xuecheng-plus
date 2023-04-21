package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * package:  com.xuecheng
 * project_name:  xuecheng-plus
 * 2023/4/17  22:10
 * description: 内容管理服务启动类
 *
 * @author wk
 * @version 1.0
 */
@SpringBootApplication
@EnableSwagger2Doc
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
