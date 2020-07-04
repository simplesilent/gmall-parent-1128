package com.atguigu.gmall.all;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * WebAllApplication
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-05
 * @Description:
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients("com.atguigu.gmall")
@ComponentScan("com.atguigu.gmall")
public class WebAllApplication8300 {
    public static void main(String[] args) {
        SpringApplication.run(WebAllApplication8300.class, args);
    }
}
