package com.atguigu.gmall.activity.service.impl;

import com.atguigu.gmall.activity.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.activity.service.SeckillGoodsService;
import com.atguigu.gmall.activity.util.CacheHelper;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.OrderRecode;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper,SeckillGoods> implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**从缓存中查询秒杀商品*/
    @Override
    public List<SeckillGoods> findAll() {
        return redisTemplate.opsForHash().values(RedisConst.SECKILL_GOODS);
    }

    /**根据skuId从缓从中获取秒杀商品*/
    @Override
    public SeckillGoods getSeckillGood(String skuId) {
        return (SeckillGoods) redisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS, skuId);
    }

    @Override
    public void seckillOrder(Long skuId, String userId) {
        // 1. 验证状态位，产品可能随时售罄，mq队列里面可能堆积了十万数据，但是已经售罄了，那么后续流程就没有必要再走了
        String publish = (String) CacheHelper.get(skuId + "");
        if ("0".equals(publish)) {
            // 已售罄
            return;
        }

        // 2.判断用户是否下过单
        //redisTemplate.opsForValue().setIfAbsent：如果存在不更新返回false，如果不存在就添加到Redis缓存返回true
        Boolean isExist = redisTemplate.opsForValue().setIfAbsent(RedisConst.SECKILL_USER + userId, skuId, RedisConst.SECKILL_TIMEOUT, TimeUnit.SECONDS);
        if (!isExist) { // 如果该用户已经秒杀成功，返回false，直接返回，不能再秒杀
            return;
        }

        // 3.秒杀----获取缓存的秒伤商品，如果能够取到，说明秒杀成功，可以下单 ，从【seckill:stock:skuId】中获取
        Integer goodsId = (Integer) redisTemplate.opsForList().rightPop(RedisConst.SECKILL_STOCK_PREFIX + skuId);
        if (goodsId == null) {
            // 商品已售罄，更新状态栏，发布通知到各个服务器
            redisTemplate.convertAndSend("seckillpush",skuId + ":0");
            return;
        }

        // 4.秒杀成功，生成预下单，放到Redis中
        OrderRecode orderRecode = new OrderRecode();
        orderRecode.setUserId(userId);
        orderRecode.setNum(1);
        orderRecode.setSeckillGoods(getSeckillGood(skuId.toString())); // 从Redis中获取秒杀商品信息
        orderRecode.setOrderStr(MD5.encrypt(userId + skuId));
        // 将预订单存到Redis中
        redisTemplate.opsForHash().put(RedisConst.SECKILL_ORDERS, userId, orderRecode);

        // 5.TODO 更新数据的库存
    }

    /**查询订单状态*/
    @Override
    public Result checkOrder(Long skuId, String userId) {
        // TODO 查询订单状态
        Boolean hasKey = redisTemplate.hasKey(RedisConst.SECKILL_USER + userId);

        return null;
    }
}
