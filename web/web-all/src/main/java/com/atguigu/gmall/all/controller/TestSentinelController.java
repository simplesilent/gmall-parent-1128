package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.test.client.TestFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestSentinelController {

    @Autowired
    private TestFeignClient testFeignClient;

    @GetMapping("/testA")
    public Result testA() {
        String s = testFeignClient.testService();
        return Result.ok(s);
    }

    @GetMapping("/testB")
    public Result testB() {
        int i = 10 / 0;
        return Result.ok("testB");
    }


}
