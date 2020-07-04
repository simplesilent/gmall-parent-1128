package com.atguigu.gmall.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author simplesilent
 * @Date: 2020/6/29 23:44
 * @Description
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("com.atguigu.gmall")
@EnableDiscoveryClient
public class ServiceTaskApplication8207 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTaskApplication8207.class, args);
    }
}
