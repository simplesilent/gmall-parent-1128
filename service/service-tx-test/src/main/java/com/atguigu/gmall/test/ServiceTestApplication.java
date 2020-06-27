package com.atguigu.gmall.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * ServiceProductApplication80
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-06
 * @Description:
 */
@SpringBootApplication
@ComponentScan({"com.atguigu"})
public class ServiceTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTestApplication.class, args);
    }
}