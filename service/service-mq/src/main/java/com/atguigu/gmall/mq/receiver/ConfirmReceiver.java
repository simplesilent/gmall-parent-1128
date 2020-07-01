package com.atguigu.gmall.mq.receiver;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ConfirmReceiver {

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
                        value = @Queue(value = "queue.confirm", autoDelete = "false"),
                        exchange = @Exchange(value = "exchange.confirm", autoDelete = "true"),
                        key = {"routing.confirm"})
                    )
    public void process(Message message, Channel channel) {
        System.out.println("RabbitListener:" + new String(message.getBody()));
        // deliveryTag:该消息的index
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        // requeue：被拒绝的是否重新入队列。
        // channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        /**
            手动确认模式，消息手动拒绝中如果requeue为true会重新放入队列，但是如果消费者在处理过程中一直抛出异常，会导致入队-》拒绝-》入队的循环，该怎么处理呢？
            第一种方法是根据异常类型来选择是否重新放入队列
            第二种方法是先成功确认，然后通过channel.basicPublish()重新发布这个消息
         */
    }

}
