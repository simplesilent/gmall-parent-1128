package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

public interface OrderInfoService {

    /**比较创建订单时生成的tradeNo和生成订单时的tradeNo是否一致，一致返回true，才可以创建订单*/
    boolean campareTradeNo(String userId, String tradeNo);

    /**获取一个tradeNo，防止表单重复提交*/
    String getTradeNo(String userId);

    /**创建订单*/
    Long saveOrderInfo(OrderInfo orderInfo,String userId);

    /**获取订单信息*/
    OrderInfo getOrderInfo(Long orderId);


    void updatePayment(OrderInfo orderInfo);

    void sendOrderStatus(OrderInfo orderInfo);
}
