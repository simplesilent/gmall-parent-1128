package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.entity.GmallCorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/mq")
public class MqApiController {

    @Autowired
    private RabbitService rabbitService;

    @RequestMapping("/sendConfirm")
    public Result sendConfirm() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        GmallCorrelationData gmallCorrelationData = new GmallCorrelationData();
        rabbitService.sendMessage("exchange.confirm", "routing.confirm", sdf.format(new Date()));
        return Result.ok();
    }

}
