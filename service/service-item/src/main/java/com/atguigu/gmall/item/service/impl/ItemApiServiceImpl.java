package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemApiService;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ItemApiServiceImpl
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-06
 * @Description:
 */
@Service
public class ItemApiServiceImpl implements ItemApiService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private ListFeignClient listFeignClient;

    /**
     * 获取sku信息，价格，分级信息，销售属性.封装成map集合
     */
    @Override
    public Map<String, Object> getItem(Long skuId) {
        return getItemMultiThread(skuId);
    }

    /**
     * 获取sku信息，价格，分级信息，销售属性.封装成map集合  ---------多线程
     */
    private Map<String, Object> getItemMultiThread(Long skuId) {

        long start = System.currentTimeMillis();

        // 封装成map
        Map<String, Object> map = new HashMap<>(5);

        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            // 获取skuInfo
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            map.put("skuInfo", skuInfo);
            return skuInfo;
        },threadPoolExecutor);

        // thenAcceptAsync : 并行消费，无返回值
        CompletableFuture<Void> categoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            // 获取分级信息
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            map.put("categoryView", categoryView);
        },threadPoolExecutor);

        CompletableFuture<Void> spuSaleAttrListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            // 获取销售属性
            List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuInfo.getSpuId(), skuId);
            map.put("spuSaleAttrList", spuSaleAttrListCheckBySku);
        },threadPoolExecutor);


        CompletableFuture<Void> skuValueIdsJSONCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            // 根据spuId 查询map 销售集合属性
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            String skuValueIdsJSON = JSON.toJSONString(skuValueIdsMap);
            map.put("valuesSkuJson", skuValueIdsJSON);
        },threadPoolExecutor);

        CompletableFuture bigDecimalCompletableFuture = CompletableFuture.runAsync(() -> {
            // 获取价格
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            map.put("price", skuPrice);
        },threadPoolExecutor);

        // 商品热度调用
        CompletableFuture incrHotScoreCompletableFuture = CompletableFuture.runAsync(() -> {
            // 每浏览一次页面，刷新一次热度
            listFeignClient.incrHotScore(skuId);
        },threadPoolExecutor);

        // allOf： 等待所有任务完成
        CompletableFuture.allOf(skuInfoCompletableFuture,
                                categoryViewCompletableFuture,
                                spuSaleAttrListCompletableFuture,
                                skuValueIdsJSONCompletableFuture,
                                bigDecimalCompletableFuture,
                                incrHotScoreCompletableFuture).join();

        long end = System.currentTimeMillis();
        System.out.println("多线程执行时间：" + (end - start) + "ms");

        return map;
    }

    /**
     * 获取sku信息，价格，分级信息，销售属性.封装成map集合 -------单线程
     */
    public Map<String, Object> getItemSingleThread(Long skuId) {

        long start = System.currentTimeMillis();

        // 获取skuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        // 获取价格
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);

        // 获取分级信息
        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());

        // 获取销售属性
        List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuInfo.getSpuId(), skuId);

        // 根据spuId 查询map 销售集合属性
        Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
        String skuValueIdsJSON = JSON.toJSONString(skuValueIdsMap);


        // 封装成map
        Map<String, Object> map = new HashMap<>(5);
        map.put("skuInfo", skuInfo);
        map.put("price", skuPrice);
        map.put("categoryView", categoryView);
        map.put("spuSaleAttrList", spuSaleAttrListCheckBySku);
        map.put("valuesSkuJson", skuValueIdsJSON);

        long end = System.currentTimeMillis();
        System.out.println("单线程执行时间：" + (end - start) + "ms");

        return map;
    }

}
