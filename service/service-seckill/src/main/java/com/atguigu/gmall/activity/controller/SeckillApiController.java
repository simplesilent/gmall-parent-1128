package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.service.SeckillGoodsService;
import com.atguigu.gmall.activity.util.CacheHelper;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserRecode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity/seckill")
public class SeckillApiController {

    @Autowired
    private RabbitService rabbitService;

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
            // 生成下单码
            String skuIdStr = MD5.encrypt(userId);
            return Result.ok(skuIdStr);
        }
        return Result.fail().message("获取下单码失败");
    }

    /**根据用户id和商品id实现秒杀下单*/
    @PostMapping("/auth/seckillOrder/{skuId}")
    public Result seckillOrder(@PathVariable("skuId") Long skuId, @RequestParam("skuIdStr") String skuIdStr ,HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        // 1.校验下单码
        if (!MD5.encrypt(userId).equals(skuIdStr)) {
            // 请求不合法
            return Result.build(null, ResultCodeEnum.SECKILL_ILLEGAL);
        }

        // 2.判断状态位
        String publish = (String) CacheHelper.get(skuId + "");
        if (StringUtils.isEmpty(publish)) {
            // 请求不合法
            return Result.build(null, ResultCodeEnum.SECKILL_ILLEGAL);
        }
        if ("0".equals(publish)) {
            // 已售罄
            return Result.build(null, ResultCodeEnum.SECKILL_FINISH);
        }

        // 3.进入秒杀
        UserRecode userRecode = new UserRecode();
        userRecode.setSkuId(skuId);
        userRecode.setUserId(userId);
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SECKILL_USER, MqConst.ROUTING_SECKILL_USER, userRecode);

        return Result.ok();
    }

    /**查询订单状态*/
    @GetMapping("/auth/checkOrder/{skuId}")
    public Result checkOrder(@PathVariable("skuId") Long skuId,HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        return seckillGoodsService.checkOrder(skuId, userId);
    }

    /**确认订单*/
    @GetMapping("/auth/trade")
    public Result<Map<String, Object>> trade(HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        Map<String, Object> map = seckillGoodsService.trade(userId);
        return Result.ok(map);
    }

    /**提交订单*/
    @PostMapping("/auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo,HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        Long orderId = seckillGoodsService.submitOrder(orderInfo,userId);
        if (orderId == null) {
            return Result.fail().message("创建订单失败");
        }
        return Result.ok(orderId);
    }
}
