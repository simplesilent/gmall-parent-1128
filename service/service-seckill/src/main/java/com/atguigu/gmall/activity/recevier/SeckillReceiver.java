package com.atguigu.gmall.activity.recevier;

import com.atguigu.gmall.activity.service.SeckillGoodsService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.user.UserRecode;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SeckillReceiver {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**商品入库*/
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_TASK),
            value = @Queue(value = MqConst.QUEUE_TASK_1,durable = "true"),
            key = {MqConst.ROUTING_TASK_1}
    ))
    public void importItemToRedis(Message message, Channel channel) throws IOException {

        // 1.将秒杀商品从数据库中查询出来
        List<SeckillGoods> seckillGoods = seckillGoodsService.list(null);

        // 2.将秒杀商品放入Redis缓从中
        for (SeckillGoods seckillGood : seckillGoods) {
            // 将秒杀商品放入hash中 【seckill:goods，skuId，seckillGood】
            redisTemplate.opsForHash().put(RedisConst.SECKILL_GOODS, seckillGood.getSkuId().toString(), seckillGood);

            // 将秒杀商品放入一个list中 【seckill:stock:skuId  skuId，skuId，skuId，skuId】
            Integer stockCount = seckillGood.getStockCount();
            for (Integer i = 0; i < stockCount; i++) {
                redisTemplate.opsForList().leftPush(RedisConst.SECKILL_STOCK_PREFIX + seckillGood.getSkuId(), seckillGood.getSkuId());
            }

            // 3.使用Redis发布消息---通知各个服务器商品已经发布
            redisTemplate.convertAndSend("seckillpush",seckillGood.getSkuId()+":1");
        }
        //channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**消费秒杀的mq消息，再次验证状态位，用户是否已经下单，判断库存，保存预下单的用户Id以及商品Id，
     并将真正下单数据放入缓存，并更新数据商品的库存数*/
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_SECKILL_USER),
            value = @Queue(value = MqConst.QUEUE_SECKILL_USER,durable = "true"),
            key = {MqConst.ROUTING_SECKILL_USER}
    ))
    public void seckillOrder(Message message, Channel channel, UserRecode userRecode) throws IOException {
        if (userRecode != null) {
            Long skuId = userRecode.getSkuId();
            String userId = userRecode.getUserId();

            /**实现秒杀下单*/
            seckillGoodsService.seckillOrder(skuId,userId);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
