package com.atguigu.gmall.common.config;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * ThreadPoolConfig
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-11
 * @Description:
 */
@Configuration
public class ThreadPoolConfig {

    /**
        线程池：
            特点：降低资源损耗，提高响应速度，提高线程的可管理性



     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(5,10,30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardPolicy());
    }


}
