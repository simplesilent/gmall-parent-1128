package com.atguigu.gmall.test.client;

import com.atguigu.gmall.test.client.impl.TestFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "service-test",fallback = TestFeignClientImpl.class)
public interface TestFeignClient {

    @GetMapping("api/test/testService")
    String testService();
}
