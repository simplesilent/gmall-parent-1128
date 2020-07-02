package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SeckillGoodsService extends IService<SeckillGoods> {

    /**从缓存中查询秒杀商品*/
    List<SeckillGoods> findAll();

    /**根据skuId从缓从中获取秒杀商品*/
    SeckillGoods getSeckillGood(String skuId);

    /**根据用户id实现秒伤下单*/
    void seckillOrder(Long skuId, String userId);

    /**查询订单状态*/
    Result checkOrder(Long skuId, String userId);
}
