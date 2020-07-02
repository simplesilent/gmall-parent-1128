package com.atguigu.gmall.common.constant;

/**
 * Redis常量配置类
 */
public class RedisConst {

    public static final String SKUKEY_PREFIX = "sku:";
    public static final String SKUKEY_SUFFIX = ":info";


    //单位：秒
    public static final long SKUKEY_TIMEOUT = 24 * 60 * 60;
    //单位：秒 尝试获取锁的最大等待时间
    public static final long SKULOCK_EXPIRE_PX1 = 1;
    //单位：秒 锁的持有时间
    public static final long SKULOCK_EXPIRE_PX2 = 1;
    public static final String SKULOCK_SUFFIX = ":lock";
    public static final long SKUKEY_TEMPORARY_TIMEOUT=60*60;

    public static final String USER_KEY_PREFIX = "user:";
    public static final String USER_CART_KEY_SUFFIX = ":cart";
    public static final long USER_CART_EXPIRE = 60 * 60 * 24 * 7;

    //用户登录
    public static final String USER_LOGIN_KEY_PREFIX = "user:login:";
    //    public static final String userinfoKey_suffix = ":info";
    public static final int USERKEY_TIMEOUT = 60 * 60 * 24 * 7;

    //秒杀商品前缀
    public static final String SECKILL_GOODS = "seckill:goods"; // 等待秒杀的商品信息
    public static final String SECKILL_ORDERS = "seckill:orders"; // 秒杀成功创建的订单
    public static final String SECKILL_ORDERS_USERS = "seckill:orders:users"; // 秒杀成功已经创建订单的用户
    public static final String SECKILL_STOCK_PREFIX = "seckill:stock:"; // 进入秒杀的库存
    public static final String SECKILL_USER = "seckill:user:"; // 判断用户是否已经创建过订单
    //用户锁定时间 单位：秒
    public static final int SECKILL_TIMEOUT = 60 * 60 * 1;

}
