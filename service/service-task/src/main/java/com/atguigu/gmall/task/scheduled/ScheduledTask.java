package com.atguigu.gmall.task.scheduled;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.entity.GmallCorrelationData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author simplesilent
 * @Date: 2020/6/29 23:46
 * @Description
 */
@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitService rabbitService;

    @Scheduled(cron = "0 23 18 * * ?")
    public void task1() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_1, "");
    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void task() {

        // 获取需要补偿的消息
        String correlationDataId = (String) redisTemplate.opsForList().rightPop(MqConst.MQ_KEY_PREFIX);
        if (!StringUtils.isEmpty(correlationDataId)) {
            // 根据correlationDataId获取从redis获取消息
            String gmallCorrelationDataStr = (String) redisTemplate.opsForValue().get(correlationDataId);
            GmallCorrelationData gmallCorrelationData = JSON.parseObject(gmallCorrelationDataStr, GmallCorrelationData.class);
            // 再次发送
            rabbitService.sendMessage(gmallCorrelationData.getExchange(), gmallCorrelationData.getRoutingKey(), gmallCorrelationData.getMessage());
        }

    }


}
