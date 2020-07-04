package com.atguigu.gmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.atguigu.gmall")
@MapperScan("com.atguigu.gmall.order.mapper")
@EnableDiscoveryClient
@EnableFeignClients("com.atguigu.gmall")
public class ServiceOrderApplication8206 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication8206.class, args);
    }
}
