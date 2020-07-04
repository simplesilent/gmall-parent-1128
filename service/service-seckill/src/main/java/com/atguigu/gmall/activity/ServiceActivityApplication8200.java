package com.atguigu.gmall.activity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.atguigu.gmall")
@MapperScan("com.atguigu.gmall.activity.mapper")
@EnableDiscoveryClient
@EnableFeignClients("com.atguigu.gmall")
public class ServiceActivityApplication8200 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceActivityApplication8200.class, args);
    }
}
