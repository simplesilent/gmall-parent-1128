package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;

import java.util.List;

/**
 * CartInoService
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-23
 * @Description:
 */
public interface CartInoService {
    /**
     * 添加购物车，并返回skuInfo到前端
     */
    SkuInfo addCart(String userId, String userTempId, Long skuId, Integer skuNum);

    List<CartInfo> cartList(String userId,String userTempId);

    void deleteCart(String userId, Long skuId);

    /**
     * 更新选中状态
     */
    void checkCart(Long skuId, Integer isChecked, String userId);

    /**获取购物车中被用户选中的*/
    List<CartInfo> getCartCheckedList(String userId);
}