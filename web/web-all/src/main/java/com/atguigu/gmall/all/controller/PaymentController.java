package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.payment.client.PaymentFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class PaymentController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private PaymentFeignClient paymentFeignClient;

    @GetMapping("pay.html")
    public String getOrderInfo(@RequestParam("orderId") Long orderId, Model model) {
        Result result = orderFeignClient.getOrderInfo(orderId);
        model.addAttribute("orderInfo",result.getData());
        return "payment/pay";
    }

    @GetMapping("pay/success.html")
    public String success() {
        return "payment/success.html";
    }

    @GetMapping("callback/return")
    public String callback(HttpServletRequest request) {
        PaymentInfo paymentInfo = new PaymentInfo();
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_no = request.getParameter("trade_no");

        paymentInfo.setCallbackContent(request.getQueryString());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setTradeNo(trade_no);
        paymentInfo.setPaymentStatus(PaymentStatus.PAID.name());
        paymentInfo.setOutTradeNo(out_trade_no);
        paymentFeignClient.updatePayment(paymentInfo);

        return "payment/success.html";
    }

}
