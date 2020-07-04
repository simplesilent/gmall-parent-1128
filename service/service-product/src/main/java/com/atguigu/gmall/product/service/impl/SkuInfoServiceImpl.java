package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * SkuInfoServiceImpl
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-03
 * @Description:
 */
@Service
public class SkuInfoServiceImpl implements SkuInfoService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ListFeignClient listFeignClient;

    @Autowired
    private RabbitService rabbitService;

    /**
     * 根据SPU的id获取销售属性集合
     *
     * @param spuInfoId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuInfoId) {
        List<SpuSaleAttr> spuSaleAttrList = skuInfoMapper.getSpuSaleAttrList(spuInfoId);
        return spuSaleAttrList;
    }

    /**
     * 根据SPU的id获取图片集合
     *
     * @param spuInfoId
     * @return
     */
    @Override
    public List<SpuImage> getSpuImageList(Long spuInfoId) {
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuInfoId);
        List<SpuImage> spuImageList = spuImageMapper.selectList(wrapper);
        return spuImageList;
    }


    /**
     * 添加SKU
     *
     * @param skuInfo
     */
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        // 1. 添加SkuInfo
        skuInfo.setIsSale(0);
        skuInfoMapper.insert(skuInfo);
        Long skuId = skuInfo.getId();

        // 2. 添加sku和 平台属性、平台属性值的关系
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }

        // 3. 添加sku和 销售属性、销售属性值的关系
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }

        // 4. 添加sku图片
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }
    }

    /**
     * 获取SKU分页集合
     *
     * @param skuInfoPage
     * @return
     */
    @Override
    public IPage<SkuInfo> getSkuInfoPage(Page<SkuInfo> skuInfoPage) {
        QueryWrapper<SkuInfo> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        IPage<SkuInfo> skuInfoIPage = skuInfoMapper.selectPage(skuInfoPage, wrapper);
        return skuInfoIPage;
    }

    /**
     * 商品SKU上架
     *
     * @param skuId
     */
    @Override
    public void onSale(Long skuId) {
//        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);

        // 通过调用service-list实现将上架商品信息保存到es中
        //listFeignClient.upperGoods(skuId);
        // 使用消息队列
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS, MqConst.ROUTING_GOODS_UPPER, skuId);

        skuInfoMapper.updateById(skuInfo);
    }

    /**
     * 商品SKU下架
     *
     * @param skuId
     */
    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);

        // 通过调用service-list实现将下架商品信息从es中删除
        //listFeignClient.lowerGoods(skuId);
        // 使用消息队列
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS, MqConst.ROUTING_GOODS_LOWER, skuId);

        skuInfoMapper.updateById(skuInfo);
    }

    /**
     * 根据skuId查询skuInfo + Redission
     *
     * @param skuId
     */
    @GmallCache
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return getSkuInfoDB(skuId);
    }

    /**
     * 根据skuId查询skuInfo
     *
     * @param skuId
     */
    public SkuInfo getSkuInfoBak(Long skuId) {

        String skuRedisKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;

        // 先从缓存中查询skuInfo信息
        String skuInfoStr = (String) redisTemplate.opsForValue().get(skuRedisKey);
        SkuInfo skuInfo = null;

        // 判断从缓从中获取的值是否为空，若为空，则从数据库中获取并向缓从中存放
        // 若不为空则返回
        if (StringUtils.isEmpty(skuInfoStr)) {
            // 如果缓从为空，则从数据库中获取，再查询数据库之前，加分布式锁
            String redisKeyLock = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX; // sku:15:lock
            String uuid = UUID.randomUUID().toString();
            Boolean OK = redisTemplate.opsForValue().setIfAbsent(redisKeyLock, uuid, 10, TimeUnit.SECONDS);

            if (OK) { // 拿到锁
                // 查询数据库
                skuInfo = getSkuInfoDB(skuId);
                // 如果查询数据库为空，那么我们设置一个合理的空值时间放入Redis缓存，让后面的缓从不至于一致访问，防止缓存穿透
                if (skuInfo == null) {
                    SkuInfo skuInfo1 = new SkuInfo();
                    redisTemplate.opsForValue().set(skuRedisKey, JSON.toJSONString(skuInfo), 60 * 60, TimeUnit.SECONDS);
                }
                // 将查询有值的同时向缓从中存放一份
                redisTemplate.opsForValue().set(skuRedisKey, JSON.toJSONString(skuInfo));
                // 再将分布式锁删除（利用lua脚本，保证get和del操作的原子性）
                DefaultRedisScript redisScript = new DefaultRedisScript();
                redisScript.setResultType(Long.class);
                redisScript.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] " +
                        "then return redis.call('del', KEYS[1]) else return 0 end");
                redisTemplate.execute(redisScript, Arrays.asList(redisKeyLock), uuid);

                // 返回数据库数据
                return skuInfo;

            } else { // 没有拿到锁
                // 等待，重新获取
                try{ TimeUnit.SECONDS.sleep(1);}catch (Exception e){e.printStackTrace();}
                return getSkuInfo(skuId);
            }
        }

        // 缓存中有数据，直接返回
        return JSON.parseObject(skuInfoStr,SkuInfo.class);
    }

    /**
     * 从数据库中查询skuInfo
     * @param skuId
     * @return
     */
    private SkuInfo getSkuInfoDB(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id", skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(wrapper);

        skuInfo.setSkuImageList(skuImages);

        return skuInfo;
    }

    /**
     * 根据skuId查询价格
     *
     * @param skuId
     */
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }
}
