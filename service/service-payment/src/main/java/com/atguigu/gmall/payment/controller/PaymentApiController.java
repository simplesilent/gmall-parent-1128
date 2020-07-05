package com.atguigu.gmall.payment.controller;

import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/payment")
public class PaymentApiController {

    @Autowired
    private PaymentService paymentService;

    /**提交支付*/
    @GetMapping("/alipay/submit/{orderId}")
    public String alipaySubmit(@PathVariable("orderId") Long orderId) {
        String form = paymentService.alipaySubmit(orderId);
        System.out.println(form);

        return form;
    }

    /**更新支付信息*/
    @PostMapping("/inner/updatePayment")
    void updatePayment(@RequestBody PaymentInfo paymentInfo) {
        paymentService.updatePayment(paymentInfo);
    }
}
