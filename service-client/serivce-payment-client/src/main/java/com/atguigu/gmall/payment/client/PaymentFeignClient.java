package com.atguigu.gmall.payment.client;

import com.atguigu.gmall.model.payment.PaymentInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author simplesilent
 * @Date: 2020/6/28 11:45
 * @Description
 */
@FeignClient("service-payment")
public interface PaymentFeignClient {

    /**更新支付信息*/
    @PostMapping("api/payment/inner/updatePayment")
    void updatePayment(@RequestBody PaymentInfo paymentInfo);
}
