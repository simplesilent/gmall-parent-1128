package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
public class OrderApiController {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private CartFeignClient cartFeignClient;

    /**获取订单信息*/
    @GetMapping("/auth/getOrderInfo")
    Result getOrderInfo(@RequestParam("orderId") Long orderId) {
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        return Result.ok(orderInfo);
    }

    // @RequestAttribute 只负责从request里面取属性值
    /**提交订单*/
    @PostMapping("/auth/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo,@RequestBody OrderInfo orderInfo, HttpServletRequest request) {
        // 获取用户id
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)) {
            return Result.fail("用户未登录");
        }

        // 从缓存中获取tradeNo，与页面传过来的进行比较，相同返回true才可以提交表单
        boolean flag = orderInfoService.campareTradeNo(userId, tradeNo);

        if (!flag) {
            return Result.fail().message("禁止重复提交");
        }
        orderInfo.setUserId(Long.parseLong(userId));
        // 保存订单
        Long orderId = orderInfoService.saveOrderInfo(orderInfo,userId);
        if (orderId != null) {
            // TODO 订单生成成功，删除已提交订单的商品
            return Result.ok(orderId);
        } else {
            return Result.fail().message("创建订单失败");
        }
    }

    /**生成订单*/
    @GetMapping("/auth/trade")
    Result<Map<String, Object>> trade(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(4);

        // TODO 购物车结算前，合并购物车功能

        // 1. 获取用户收货地址
        Result<List<UserAddress>> result = userFeignClient.getUserAddressList();
        map.put("userAddressList",result.getData());

        // 2. 获取订单详情（将被选中的购物车封装到订单信息中）
        Result<List<CartInfo>> result1 =  cartFeignClient.getCartCheckedList();
        List<CartInfo> cartInfoList = result1.getData();
        // 购物车封装到订单列表
        AtomicReference<Integer> totalNum = new AtomicReference<>(0);
        List<OrderDetail> orderDetails = new ArrayList<>();
        orderDetails = cartInfoList.stream().map(cartInfo -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            totalNum.set(totalNum.get() + cartInfo.getSkuNum());
            return orderDetail;
        }).collect(Collectors.toList());

        map.put("detailArrayList", orderDetails);

        // 3.获取商品数量和商品总价
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetails);
        orderInfo.sumTotalAmount();
        map.put("totalNum", totalNum);
        map.put("totalAmount", orderInfo.getTotalAmount());

        // 向页面放一个交易交流流水号tradeNo，防止页面重复提交
        String userId = AuthContextHolder.getUserId(request);
        String tradeNo = orderInfoService.getTradeNo(userId);
        map.put("tradeNo", tradeNo);

        return Result.ok(map);
    }
}
