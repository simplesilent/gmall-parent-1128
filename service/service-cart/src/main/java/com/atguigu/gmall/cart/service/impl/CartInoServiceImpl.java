package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartInoService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sun.swing.StringUIClientPropertyKey;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

/**
 * CartInoServiceImpl
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-23
 * @Description:
 */
@Service
@Slf4j
public class CartInoServiceImpl implements CartInoService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加购物车，并返回skuInfo到前端
     */
    @Override
    public SkuInfo addCart(String userId, String userTempId, Long skuId, Integer skuNum) {

        if (StringUtils.isEmpty(userId)) {
            userId = userTempId;
        }
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        // 从数据库查询CartInfo
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        wrapper.eq("sku_id", skuId);
        CartInfo cartInfo = cartInfoMapper.selectOne(wrapper);

        // 判断购物车信息是否存在
        if (cartInfo != null) {
            // 存在即更新
            // 更新价格
            cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
            cartInfo.setCartPrice(skuInfo.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum() + skuNum)));
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfoMapper.updateById(cartInfo);
        } else {
            // 不存在，向数据库中添加
            cartInfo = new CartInfo();
            cartInfo.setUserId(userId);
            cartInfo.setSkuId(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice().multiply(new BigDecimal(skuNum)));
            cartInfo.setSkuNum(skuNum);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setIsChecked(1);
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfoMapper.insert(cartInfo);
        }

        // 向redis缓从存放或者更新
        redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX,
                skuId.toString(), cartInfo);

        return skuInfo;
    }

    @Override
    public List<CartInfo> cartList(String userId,String userTempId) {
        List<CartInfo> cartInfoList = new ArrayList<>();

        // 未登录：根据临时用户ID获取未登录的购物车数据
        if (StringUtils.isEmpty(userId)) {
            cartInfoList = this.getCartList(userTempId);
            return cartInfoList;
        }

        // 已登录 ----合并购物车
        List<CartInfo> cartInfoListByUserTmpId = this.getCartList(userTempId);
        if (cartInfoListByUserTmpId.size() == 0) {
            // 如果未登录购物车中没有数据，直接返回用户已有的购物车数据
            cartInfoList = this.getCartList(userId);
            return cartInfoList;
        }
        //  如果未登录购物车中有数据，则进行合并
        this.mergeToCartList(cartInfoListByUserTmpId,userId,userTempId);
        cartInfoList = this.getCartList(userId);

        return cartInfoList;
    }

    /**合并购物车*/
    private void mergeToCartList(List<CartInfo> cartInfoListByUserTmpId, String userId,String userTempId) {
        // 用户已登录购物车
        List<CartInfo> cartInfoListLogin = this.getCartList(userId);
        Map<Long, CartInfo> cartInfoMapLogin = cartInfoListLogin.stream().collect(Collectors.toMap(CartInfo::getSkuId, cartInfo -> cartInfo));
        // 遍历未登录购物车数据
        for (CartInfo cartInfoNoLogin : cartInfoListByUserTmpId) {
            Long skuId = cartInfoNoLogin.getSkuId();
            if (cartInfoMapLogin.containsKey(skuId)) { // 已登录购物车数据和未登录有着相同的商品skuId
                // 将sku商品数量相加
                CartInfo cartInfoLogin = cartInfoMapLogin.get(skuId);
                cartInfoLogin.setSkuNum(cartInfoLogin.getSkuNum() + cartInfoNoLogin.getSkuNum());
                // 更新勾选数据
                if (cartInfoNoLogin.getIsChecked() == 1) {
                    cartInfoLogin.setIsChecked(1);
                }
                // 更新数据库
                cartInfoMapper.updateById(cartInfoLogin);
                // 更新缓存
                BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
                cartInfoLogin.setSkuPrice(skuPrice);
                redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX,
                        skuId.toString(),cartInfoLogin);

            } else {
                // 已经登录的购物车数据没有包含未登录购物车数据
                // 重新插入到数据库中
                cartInfoNoLogin.setId(null);
                cartInfoNoLogin.setUserId(userId);
                cartInfoMapper.insert(cartInfoNoLogin);
                // 同步缓存
                BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
                cartInfoNoLogin.setSkuPrice(skuPrice);
                redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX,
                        skuId.toString(),cartInfoNoLogin);
            }
            // 删除用户临时购物车数据
            this.deleteCart(userTempId,skuId);
        }
    }

    /**查询购物车*/
    private List<CartInfo> getCartList(String userIdParam) {
        List<CartInfo> cartInfoList = null;
        // 先从缓存中查询
        cartInfoList = redisTemplate.opsForHash().values(RedisConst.USER_KEY_PREFIX + userIdParam + RedisConst.USER_CART_KEY_SUFFIX);

        // 缓存中有直接返回，缓存没有，查询数据库同步更新到缓存
        if (cartInfoList.size() == 0) {
            QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userIdParam);
            List<CartInfo> cartInfoListOfDB = cartInfoMapper.selectList(wrapper);
            for (CartInfo cartInfo : cartInfoListOfDB) {
                BigDecimal skuPrice = productFeignClient.getSkuPrice(cartInfo.getSkuId());
                cartInfo.setSkuPrice(skuPrice);
                // 更新到缓存
                redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX + userIdParam + RedisConst.USER_CART_KEY_SUFFIX,
                        cartInfo.getSkuId().toString(),cartInfo);
                cartInfoList.add(cartInfo);
            }
        }
        return cartInfoList;
    }

    /**删除购物车信息*/
    @Override
    public void deleteCart(String userId, Long skuId) {
        // 删除数据库中数据
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("sku_id",skuId);
        cartInfoMapper.delete(wrapper);

        // 删除缓存中数据
        redisTemplate.opsForHash().delete(RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX,skuId.toString());
    }

    /**
     * 更新选中状态
     */
    @Override
    public void checkCart(Long skuId, Integer isChecked, String userId) {
        // 更新数据库中的 选中状态 由 1-> 0
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("sku_id",skuId);
        CartInfo cartInfo = cartInfoMapper.selectOne(wrapper);

        // 判断数据库选中状态
        cartInfo.setIsChecked(isChecked);
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        cartInfo.setSkuPrice(skuInfo.getPrice());
        cartInfoMapper.update(cartInfo, wrapper);

        // 同步缓存
        redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX,
                skuId.toString(), cartInfo);
    }

    /**获取购物车中被用户选中的商品*/
    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        // 先从缓存中获取
        List<CartInfo> cartCacheInfoList = redisTemplate.opsForHash().values(
                RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX);

        // 缓存中没有从数据中获取
        if (cartCacheInfoList != null && cartInfoList.size() >= 0) {
           cartCacheInfoList.forEach(cartInfo -> {
               if (cartInfo.getIsChecked().intValue() == 1) {
                   cartInfoList.add(cartInfo);
               }
           });
        }
        return cartInfoList;
    }
}
