package com.atguigu.gmall.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.atguigu.gmall")
@MapperScan("com.atguigu.gmall.payment.mapper")
@EnableDiscoveryClient
public class ServicePaymentApplication8207 {
    public static void main(String[] args) {
        SpringApplication.run(ServicePaymentApplication8207.class, args);
    }
}
