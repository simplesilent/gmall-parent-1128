package com.atguigu.gmall.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/payment")
public class PaymentApiController {

    @GetMapping("/alipay/submit/{orderId}")
    public String payment(@PathVariable("orderId") Long orderId) {
        return "form表单";
    }
}
