package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * PassportApiController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-22
 * @Description:
 */
@RestController
@RequestMapping("/api/user/passport")
public class PassportApiController {

    @Autowired
    private UserService userService;

    /**用户登录*/
    @PostMapping("/login")
    public Result login(@RequestBody UserInfo userInfo) {
        // 根据用户登录的信息从数据库中获取用户信息
        UserInfo info = userService.login(userInfo);

        if (info == null) {
            return Result.fail().message("用户名或者密码错误");
        }

        // 随机生成一个token
        String token = UUID.randomUUID().toString().replace("-", "");
        // 将token向redis中存放一份
        userService.putToken(token, info.getId() + "");
        // 将数据封装返回
        Map<String, Object> map = new HashMap<>(3);
        map.put("name", info.getName());
        map.put("nickName", info.getNickName());
        map.put("token", token);
        return Result.ok(map);
    }

    /**登出*/
    @GetMapping("/logout")
    public Result logout(HttpServletRequest request) {
        userService.logout(request);
        return Result.ok();
    }

    /**
     * 根据token获取userId
     * @param token
     * @return
     */
    @GetMapping("/getUserIdByToken/{token}")
    String getUserIdByToken(@PathVariable("token") String token) {
        return userService.getUserIdByToken(token);
    }

}
