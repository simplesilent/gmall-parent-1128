package com.atguigu.gmall.list.receiver;

import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.list.service.SearchService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ListReceiver {

    @Autowired
    private SearchService searchService;

    /**
     * 使用消息队列处理商品上架
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_GOODS),
            value = @Queue(value = MqConst.QUEUE_GOODS_UPPER,durable = "true"),
            key = {MqConst.ROUTING_GOODS_UPPER} ))
    public void upperGoods(Long skuId, Message message, Channel channel) {
        try {
            searchService.upperGoods(skuId);
            // 成功，确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 使用消息队列处理商品下架
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_GOODS),
            value = @Queue(value = MqConst.QUEUE_GOODS_LOWER,durable = "true"),
            key = {MqConst.ROUTING_GOODS_LOWER}
    ))
    public void lowerGoods(Long skuId, Message message, Channel channel) {
        try {
            searchService.lowerGoods(skuId);
            // 成功，确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
