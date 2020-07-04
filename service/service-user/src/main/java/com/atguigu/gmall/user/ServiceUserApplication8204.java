package com.atguigu.gmall.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * ServiceUserApplication8208
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-22
 * @Description:
 */
@SpringBootApplication
@ComponentScan("com.atguigu.gmall")
@MapperScan({"com.atguigu.gmall.user.mapper"})
@EnableDiscoveryClient
public class ServiceUserApplication8204 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication8204.class, args);
    }
}
