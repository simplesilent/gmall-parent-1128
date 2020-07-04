package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author simplesilent
 * @Date: 2020/6/25 14:42
 * @Description
 */
@RestController
@RequestMapping("/api/user/")
public class UserApiController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户地址
     */
    @GetMapping("/inner/getUserAddressList")
    Result<List<UserAddress>> getUserAddressList(HttpServletRequest request) {
        // 获取用户id
        String userId = AuthContextHolder.getUserId(request);
        List<UserAddress> userAddressList = userService.getUserAddressList(userId);
        return Result.ok(userAddressList);
    }
}
