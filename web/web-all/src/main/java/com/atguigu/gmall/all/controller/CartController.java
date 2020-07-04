package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * CartController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-23
 * @Description:
 */
@Controller
public class CartController {

    @Autowired
    private CartFeignClient cartFeignClient;

    @RequestMapping("addCart.html")
    public String addCart(CartInfo cartInfo, Model model, HttpServletRequest request) {

        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);

        if (!Objects.equals(null, cartInfo.getSkuId()) && !Objects.equals(null, cartInfo.getSkuNum())) {
            // 调用feign添加商品到购物车
            Result<Map> result = cartFeignClient.addCart(cartInfo.getSkuId(), cartInfo.getSkuNum());
            model.addAllAttributes(result.getData());
        }

        return "cart/addCart";
    }

    @RequestMapping("cart.html")
    public String cartList(Model model) {
//        Result<CartInfo> result = cartFeignClient.cartList();
//        model.addAttribute("data",result.getData());
        return "cart/index";
    }

}
