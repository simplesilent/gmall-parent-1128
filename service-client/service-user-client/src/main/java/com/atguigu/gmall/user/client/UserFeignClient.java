package com.atguigu.gmall.user.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * UserFeignClient
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-22
 * @Description:
 */
@FeignClient("service-user")
public interface UserFeignClient {

    @GetMapping("/api/user/passport/getUserIdByToken/{token}")
    String getUserIdByToken(@PathVariable("token") String token);

    /**获取用户收货地址*/
    @GetMapping("/api/user/inner/getUserAddressList")
    Result<List<UserAddress>> getUserAddressList();
}