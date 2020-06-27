package com.atguigu.gmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * ServiceProductApplication80
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-06
 * @Description:
 */
@SpringBootApplication
@ComponentScan({"com.atguigu.gmall"})
@MapperScan("com.atguigu.gmall.product.mapper")
@EnableDiscoveryClient
@EnableFeignClients("com.atguigu.gmall")
public class ServiceProductApplication8201 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication8201.class, args);
    }
}