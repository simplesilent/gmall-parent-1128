package com.atguigu.gmall.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * ServiceCartApplication8005
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-23
 * @Description:
 */
@SpringBootApplication
@ComponentScan("com.atguigu.gmall")
@MapperScan({"com.atguigu.gmall.cart.mapper"})
@EnableDiscoveryClient
@EnableFeignClients("com.atguigu.gmall")
public class ServiceCartApplication8205 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCartApplication8205.class, args);
    }
}
