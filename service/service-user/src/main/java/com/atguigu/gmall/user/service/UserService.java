package com.atguigu.gmall.user.service;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * UserService
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-22
 * @Description:
 */
public interface UserService {

    /**
     * 获取登录信息
     */
    UserInfo login(UserInfo userInfo);

    /**
     * 向Redis中存放token
     */
    void putToken(String token, String userId);

    /**登出*/
    void logout(HttpServletRequest request);

    /**根据token获取用户ID*/
    String getUserIdByToken(String token);

    /**根据用户ID获取收货地址*/
    List<UserAddress> getUserAddressList(String userId);
}