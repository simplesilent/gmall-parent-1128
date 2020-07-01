package com.atguigu.gmall.constant;

public class MqConst {

    /**
     * 消息补偿
     */
    public static final String MQ_KEY_PREFIX = "mq:list";
    public static final int RETRY_COUNT = 3;


    /**
     * 商品上下架
     */
    public static final String EXCHANGE_DIRECT_GOODS = "exchange.direct.goods";
    public static final String ROUTING_GOODS_UPPER = "goods.upper";
    public static final String ROUTING_GOODS_LOWER = "goods.lower";
    //队列
    public static final String QUEUE_GOODS_UPPER  = "queue.goods.upper";
    public static final String QUEUE_GOODS_LOWER  = "queue.goods.lower";

    /**
     * 订单支付
     */
    public static final String EXCHANGE_DIRECT_PAYMENT_PAY = "exchange.direct.payment.pay";
    public static final String ROUTING_PAYMENT_PAY = "payment.pay";
    //队列
    public static final String QUEUE_PAYMENT_PAY  = "queue.payment.pay";
    /**
     * 减库存
     */
    public static final String EXCHANGE_DIRECT_WARE_STOCK = "exchange.direct.ware.stock";
    public static final String ROUTING_WARE_STOCK = "ware.stock";
    //队列
    public static final String QUEUE_WARE_STOCK  = "queue.ware.stock";
    /**
     * 减库存成功，更新订单状态
     */
    public static final String EXCHANGE_DIRECT_WARE_ORDER = "exchange.direct.ware.order";
    public static final String ROUTING_WARE_ORDER = "ware.order";
    //队列
    public static final String QUEUE_WARE_ORDER  = "queue.ware.order";

    /**
     * 关闭交易
     */
    public static final String EXCHANGE_DIRECT_PAYMENT_CLOSE = "exchange.direct.payment.close";
    public static final String ROUTING_PAYMENT_CLOSE = "payment.close";
    //队列
    public static final String QUEUE_PAYMENT_CLOSE  = "queue.payment.close";

    /**
     * 定时任务，发送入库通知
     */
    public static final String EXCHANGE_DIRECT_TASK = "exchange.direct.task";
    public static final String ROUTING_TASK_1 = "seckill.task.1";
    public static final String QUEUE_TASK_1  = "queue.task.1";


    /**
     * 秒杀，用于通知创建秒杀订单
     */
    public static final String EXCHANGE_DIRECT_SECKILL_USER = "exchange.direct.seckill.user";
    public static final String ROUTING_SECKILL_USER = "seckill.user";
    public static final String QUEUE_SECKILL_USER  = "queue.seckill.user";

    /**
     * 定时任务
     */
    public static final String ROUTING_TASK_18 = "seckill.task.18";
    public static final String QUEUE_TASK_18  = "queue.task.18";



}
