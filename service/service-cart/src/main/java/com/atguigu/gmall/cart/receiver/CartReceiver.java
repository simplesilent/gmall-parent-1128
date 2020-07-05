package com.atguigu.gmall.cart.receiver;

import com.atguigu.gmall.cart.service.CartInoService;
import com.atguigu.gmall.constant.MqConst;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CartReceiver {

    @Autowired
    private CartInoService cartInoService;

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_CART_USER),
            value = @Queue(value = MqConst.QUEUE_CART_USER,durable = "true"),
            key = {MqConst.ROUTING_CART_USER}
    ))
    public void mergeCart(Message message, Channel channel, Map<String,String> map) throws IOException {
        String userId = map.get("userId");
        String userTempId = map.get("userTempId");
        try {
            if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(userTempId)) {
                cartInoService.mergeCart(userId,userTempId);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

}
