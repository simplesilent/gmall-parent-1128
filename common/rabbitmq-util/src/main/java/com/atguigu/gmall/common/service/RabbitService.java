package com.atguigu.gmall.common.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.entity.GmallCorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    public boolean sendMessage(String exchange, String routingKey, Object message) {

        // 封装一个数据GmallCorrelationData，放入缓存
        GmallCorrelationData gmallCorrelationData = new GmallCorrelationData();
        String correlationDataId = UUID.randomUUID().toString();
        gmallCorrelationData.setId(correlationDataId);
        gmallCorrelationData.setExchange(exchange);
        gmallCorrelationData.setRoutingKey(routingKey);
        gmallCorrelationData.setMessage(message);

        // 保存原始文本和id到缓存，方便出现出现问题处理
        redisTemplate.opsForValue().set(correlationDataId,JSON.toJSONString(gmallCorrelationData),4, TimeUnit.HOURS);

        // 使用携带CorrelationData有消息id的队列发送
        rabbitTemplate.convertAndSend(exchange, routingKey, message, gmallCorrelationData);

        return true;
    }
}
