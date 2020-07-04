package com.atguigu.gmall.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TestApiSentinelController {

    @GetMapping("api/test/testService")
    String testService() {
        /*int i = 10 / 0;*/
        return "testService....(#^.^#)";
    }
}
