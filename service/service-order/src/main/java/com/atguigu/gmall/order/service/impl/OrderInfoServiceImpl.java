package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.HttpClientUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.user.client.UserFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderInfoServiceImpl implements OrderInfoService {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private CartFeignClient cartFeignClient;

    @Value("${ware.url}")
    private String WARE_URL;

    /**根据订单id获取订单信息*/
    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        // 查询订单详情
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("order_id", orderId);
        List orderDetailList = orderDetailMapper.selectList(wrapper);
        orderInfo.setOrderDetailList(orderDetailList);
        return orderInfo;
    }

    /**比较tradeNo*/
    @Override
    public boolean campareTradeNo(String userId,String tradeNo) {
        boolean flag = false;
        // 从缓存中获取tradeNo
        String tradeNoOfCache = (String) redisTemplate.opsForValue().get("user:"+userId+":tradeNo");
//        if (tradeNo != null && tradeNo.equals(tradeNoOfCache)) {
//            redisTemplate.delete("user" + userId + "tradeNo");
//            flag = true;
//        }
        // lua脚本,同时，删除缓存中的tradeNo
        DefaultRedisScript redisScript = new DefaultRedisScript();
        redisScript.setResultType(Long.class);
        redisScript.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] " +
                "then return redis.call('del', KEYS[1]) else return 0 end");
        Long execute = (Long) redisTemplate.execute(redisScript, Arrays.asList("user:"+userId+":tradeNo"), tradeNo);
        if (execute == 1) {
            // 执行成功，相同返回true
            flag = true;
        }

        return flag;
    }

    /**向缓存中存放tradeNo，防止表单重复提交*/
    @Override
    public String getTradeNo(String userId) {
        String tradeNo = UUID.randomUUID().toString().replace("-", "");
        // TODO redisTemplate 的了解
        // 存放到缓存
        redisTemplate.opsForValue().set("user:"+userId+":tradeNo",tradeNo,15, TimeUnit.MINUTES);
        return tradeNo;
    }

    /**保存订单*/
    @Override
    public Long saveOrderInfo(OrderInfo orderInfo,String userId) {
        // 查询用户地址
        Result<List<UserAddress>> userAddressList = userFeignClient.getUserAddressList();
        UserAddress userAddress = userAddressList.getData().get(0);

        // 1.封装订单信息OrderInfo
        orderInfo.setConsignee(userAddress.getConsignee());
        orderInfo.setConsigneeTel(userAddress.getPhoneNum());
        orderInfo.setDeliveryAddress(userAddress.getUserAddress());
        orderInfo.sumTotalAmount();
        orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
        orderInfo.setCreateTime(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        orderInfo.setExpireTime(calendar.getTime()); // 失效时间
        // 外部订单交易编号 outTreadeNo
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = format.format(new Date());
        String outTreadeNo = "ATGUIGU"+ System.currentTimeMillis() + ""+ date;
        orderInfo.setOutTradeNo(outTreadeNo);
        // 订单描述 TradeBody
        StringBuffer tradeBody = new StringBuffer();
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            tradeBody.append(orderDetail.getSkuName());
        }
        if (tradeBody.toString().length() > 100) {
            orderInfo.setTradeBody(tradeBody.toString().substring(0, 100));
        } else {
            orderInfo.setTradeBody(tradeBody.toString());
        }
        OrderDetail orderDetail1 = orderInfo.getOrderDetailList().get(0);
        orderInfo.setImgUrl(orderDetail1.getImgUrl());

        // TODO 了解webservice，HttpClient的使用
        // http://localhost:9001/hasStock?skuId=13&num=10
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            //  查询库存,判断当前当前库存是否大于购物数量，
            String isHashStock = HttpClientUtil.doGet(WARE_URL + "/hasStock?skuId=" + orderDetail.getSkuId() + "&num=" + orderDetail.getSkuNum());
            if ("0".equals(isHashStock)) {
                return null;
            }

            // 核对够买的商品数量和购物车数量是否一致
            BigDecimal skuPrice = productFeignClient.getSkuPrice(orderDetail.getSkuId());
            if (orderDetail.getOrderPrice().compareTo(skuPrice) != 0) {
                return null;
            }
        }

        // 插入数据库
        int insert = orderInfoMapper.insert(orderInfo);

        Long orderInfoId = null;
        if (insert != 0) {
           orderInfoId = orderInfo.getId();
            for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
                orderDetail.setOrderId(orderInfoId);
                orderDetailMapper.insert(orderDetail);
            }
        }

        // 订单生成功，删除购物中的数据
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            cartFeignClient.deleteCart(orderDetail.getSkuId());
        }

        return orderInfoId;
    }
}
