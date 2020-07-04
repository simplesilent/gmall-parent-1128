package com.atguigu.gmall.seckill.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient("service-seckill")
public interface SeckillFeignClient {

    @GetMapping("/api/activity/seckill/findAll")
    Result<List<SeckillGoods>> findAll();

    @GetMapping("/api/activity/seckill/getSeckillGoods/{skuId}")
    Result<SeckillGoods> getSeckillGoods(@PathVariable("skuId") String skuId);

    @GetMapping("/api/activity/seckill/auth/trade")
    Result<Map<String, Object>> trade();
}
