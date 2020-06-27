package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartInoService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * CartController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-23
 * @Description:
 */
@RestController
@RequestMapping("/api/cart/")
public class CartApiController {

    @Autowired
    private CartInoService cartInoService;

    @PostMapping("/addToCart/{skuId}/{skuNum}")
    Result<Map> addCart(@PathVariable("skuId") Long skuId,
                        @PathVariable("skuNum") Integer skuNum,
                        HttpServletRequest request) {

        // 获取用户id
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);

        // 调用service，添加购物车，并返回skuInfo到前端
        SkuInfo skuInfo = cartInoService.addCart(userId, userTempId, skuId, skuNum);

        Map<String, Object> map = new HashMap<>();
        map.put("skuNum", skuNum);
        map.put("skuInfo", skuInfo);
        return Result.ok(map);
    }

    /**
     * 购物车列表
     */
    @GetMapping("/cartList")
    Result cartList(HttpServletRequest request) {

        // 获取用户id
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);

        if (StringUtils.isEmpty(userId)) {
            userId = userTempId;
        }
        List<CartInfo> cartInfos = cartInoService.cartList(userId);
        return Result.ok(cartInfos);
    }

    /**
     * 删除
     */
    @DeleteMapping("/deleteCart/{skuId}")
    Result deleteCart(HttpServletRequest request, @PathVariable("skuId") Long skuId) {

        // 获取用户id
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);

        if (StringUtils.isEmpty(userId)) {
            userId = userTempId;
        }

        // 调用service删除购物车信息
        cartInoService.deleteCart(userId, skuId);

        return Result.ok();
    }

    /**更新选中状态*/
    @GetMapping("/checkCart/{skuId}/{isChecked}")
    Result checkCart(@PathVariable("skuId") Long skuId,@PathVariable("isChecked") Integer isChecked,HttpServletRequest request) {

        // 获取用户id
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);

        if (StringUtils.isEmpty(userId)) {
            userId = userTempId;
        }

        cartInoService.checkCart(skuId, isChecked, userId);
        return Result.ok();
    }

    /**获取购物车中被用户选中的*/
    @GetMapping("/getCartCheckedList")
    Result<List<CartInfo>> getCartCheckedList(HttpServletRequest request) {
        // 获取用户id
        String userId = AuthContextHolder.getUserId(request);
        List<CartInfo> cartInfoList = cartInoService.getCartCheckedList(userId);
        return Result.ok(cartInfoList);
    }
}
