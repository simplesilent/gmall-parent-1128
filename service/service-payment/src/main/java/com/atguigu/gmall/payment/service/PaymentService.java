package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.payment.PaymentInfo;

/**
 * @Author simplesilent
 * @Date: 2020/6/28 09:49
 * @Description
 */
public interface PaymentService {

    /**点击支付，支付宝返回支付页面*/
    String alipaySubmit(Long orderId);

    /**更新支付信息*/
    void updatePayment(PaymentInfo paymentInfo);
}
