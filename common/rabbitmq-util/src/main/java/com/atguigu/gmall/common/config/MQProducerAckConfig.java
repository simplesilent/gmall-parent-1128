package com.atguigu.gmall.common.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.entity.GmallCorrelationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MQProducerAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * @param correlationData correlation data for the callback.
     * @param ack             true for ack, false for nack
     * @param cause           An optional cause, for nack, when available, otherwise null.
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息发送成功" + JSON.toJSONString(correlationData));
        } else {
            // 【消息没到交换机】消息发投递到交换机失败
            log.info("消息发送失败" + JSON.toJSONString(correlationData));

            // 消息发送失败，从缓从中取出原始文本
            String gmallCorrelationDataStr = (String) redisTemplate.opsForValue().get(correlationData.getId());
            addRetry(JSON.parseObject(gmallCorrelationDataStr, GmallCorrelationData.class));

        }
    }

    /**
     * @param message    the returned message.
     * @param replyCode  the reply code.
     * @param replyText  the reply text.
     * @param exchange   the exchange.
     * @param routingKey the routing key.
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        // 【消息到达了路由】，投递失败（消费者绑定的路由或者交换机不一致等等。。。）
        System.out.println("消息主体: "+ new String(message.getBody()));
        System.out.println("应答码: "+ replyCode);
        System.out.println("描述："+ replyText);
        System.out.println("消息使用的交换器 exchange : "+ exchange);
        System.out.println("消息使用的路由键 routing : "+ routingKey);

        // 消息发送失败
        // spring_listener_return_correlation：该属性是用来确定消息被退回时调用哪个监听器
        // spring_returned_message_correlation：该属性是指退回待确认消息的唯一标识，CorrelationData的id

        String correlationDataId = (String) message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        String gmallCorrelationDataStr = (String) redisTemplate.opsForValue().get(correlationDataId);
        addRetry(JSON.parseObject(gmallCorrelationDataStr, GmallCorrelationData.class));
    }

    /**三次重试机会*/
    private void addRetry(GmallCorrelationData gmallCorrelationData) {
        int retryCount = gmallCorrelationData.getRetryCount();
        gmallCorrelationData.setRetryCount(++retryCount);
        if (retryCount <= 3) {
            // 将需要补偿的消息id放入Redis中
            redisTemplate.opsForList().leftPush(MqConst.MQ_KEY_PREFIX, gmallCorrelationData.getId());
            // 将缓存中的已有的次数更新
            redisTemplate.opsForValue().set(gmallCorrelationData.getId(), JSON.toJSONString(gmallCorrelationData),4, TimeUnit.HOURS);
        }
    }

}
