package com.atguigu.gmall.comment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.atguigu.gmall.comment.mapper")
@EnableDiscoveryClient
@EnableFeignClients("com.atguigu.gmall")
public class ServiceCommentApplication8208 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCommentApplication8208.class, args);
    }
}
