package com.atguigu.gmall.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DeadLetterMqConfig {

    public static final String EXCHANGE_DEAD = "exchange.dead";
    public static final String ROUTING_DEAD_1 = "routing.dead.1";
    public static final String ROUTING_DEAD_2 = "routing.dead.2";
    public static final String QUEUE_DEAD_1 = "queue.dead.1";
    public static final String QUEUE_DEAD_2 = "queue.dead.2";

    /**
     * 其他队列可以在RabbitListener上面做绑定
     * @return
     */

    @Bean
    public DirectExchange exchange() {

        return new DirectExchange(EXCHANGE_DEAD, true, false, null);
    }

    @Bean
    public Queue queue1() {
        Map<String, Object> arguments = new HashMap<>(3);
        arguments.put("x-dead-letter-exchange", EXCHANGE_DEAD);
        arguments.put("x-dead-letter-routing-key", ROUTING_DEAD_2);
        //方式二，统一延迟时间
        arguments.put("x-message-ttl", 10 * 1000);

        return new Queue(QUEUE_DEAD_1, true, false, false, arguments);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue1()).to(exchange()).with(ROUTING_DEAD_1);
    }

    @Bean
    public Queue queue2() {
        return new Queue(QUEUE_DEAD_2, true, false, false, null);
    }

    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(queue2()).to(exchange()).with(ROUTING_DEAD_2);
    }
}