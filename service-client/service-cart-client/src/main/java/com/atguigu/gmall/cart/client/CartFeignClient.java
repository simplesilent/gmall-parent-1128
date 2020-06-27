package com.atguigu.gmall.cart.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

/**
 * CartFeignClient
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-23
 * @Description:
 */
@FeignClient(value = "service-cart")
public interface CartFeignClient {

    @PostMapping("/api/cart/addToCart/{skuId}/{skuNum}")
    Result<Map> addCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Integer skuNum);

    @GetMapping("/api/cart/cartList")
    Result cartList();

    @GetMapping("/api/cart/getCartCheckedList")
    Result<List<CartInfo>> getCartCheckedList();
}