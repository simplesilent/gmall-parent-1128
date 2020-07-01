package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.service.SeckillGoodsService;
import com.atguigu.gmall.activity.util.CacheHelper;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/activity/seckill")
public class SeckillApiController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /**从缓存中查询秒杀商品*/
    @GetMapping("/findAll")
    public Result<List<SeckillGoods>> findAll() {
        List<SeckillGoods> seckillGoodsList = seckillGoodsService.findAll();
        return Result.ok(seckillGoodsList);
    }

    /**根据skuId从缓从中获取秒杀商品*/
    @GetMapping("/getSeckillGoods/{skuId}")
    public Result<SeckillGoods> getSeckillGoods(@PathVariable("skuId") String skuId) {
        SeckillGoods seckillGoods = seckillGoodsService.getSeckillGood(skuId);
        return Result.ok(seckillGoods);
    }

    /**获取下单码*/
    @GetMapping("/auth/getSeckillSkuIdStr/{skuId}")
    public Result getSeckillSkuIdStr(@PathVariable("skuId") String skuId, HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);

        String publish = (String) CacheHelper.get(skuId);
        if (!StringUtils.isEmpty(publish) && "1".equals(publish)) {
            //
            String skuIdStr = MD5.encrypt(userId);
            return Result.ok(skuIdStr);
        }
        return Result.fail().message("获取下单码失败");
    }

}
