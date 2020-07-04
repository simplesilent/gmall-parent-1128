package com.atguigu.gmall.test.client.impl;

import com.atguigu.gmall.test.client.TestFeignClient;
import org.springframework.stereotype.Component;

@Component
public class TestFeignClientImpl implements TestFeignClient {
    @Override
    public String testService() {
        return "熔断降级处理-----o(╥﹏╥)o。。。。。";
    }
}
