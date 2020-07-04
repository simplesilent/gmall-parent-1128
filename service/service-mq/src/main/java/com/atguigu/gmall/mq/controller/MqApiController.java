package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.common.config.DelayedMqConfig;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.entity.GmallCorrelationData;
import com.atguigu.gmall.common.config.DeadLetterMqConfig;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendConfirm")
    public Result sendConfirm() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        GmallCorrelationData gmallCorrelationData = new GmallCorrelationData();
        rabbitService.sendMessage("exchange.confirm", "routing.confirm", sdf.format(new Date()));
        return Result.ok();
    }

    @RequestMapping("/sendDeadLetter")
    public Result sendDeadLetter() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rabbitService.sendMessage(DeadLetterMqConfig.EXCHANGE_DEAD,DeadLetterMqConfig.ROUTING_DEAD_1, sdf.format(new Date()));
        return Result.ok();
    }

    @RequestMapping("/sendDelayLetter")
    public Result sendDelayLetter() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rabbitTemplate.convertAndSend(DelayedMqConfig.EXCHANGE_DELAY, DelayedMqConfig.ROUTING_DELAY, sdf.format(new Date()), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setDelay(10*1000);
                return message;
            }
        });
        return Result.ok();
    }

}
