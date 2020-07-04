package com.atguigu.gmall.test.controler;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.test.service.TestService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-09
 * @Description:
 */
@Api(description = "测试接口")
@RestController
@RequestMapping()
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("testLock")
    public Result testLock() {
        String s = testService.testLock();
        return Result.ok(s);
    }

}
