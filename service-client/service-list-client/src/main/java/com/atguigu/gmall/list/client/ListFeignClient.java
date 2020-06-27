package com.atguigu.gmall.list.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.SearchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ListFeignClient
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-16
 * @Description:
 */
@FeignClient("service-list")
public interface ListFeignClient {

    /**
     * 商品上架
     * @param skuId
     */
    @GetMapping("/api/list/inner/upperGoods/{skuId}")
    Result upperGoods(@PathVariable("skuId") Long skuId);

    /**
     * 商品下架
     * @param skuId
     */
    @GetMapping("/api/list/inner/lowerGoods/{skuId}")
    Result lowerGoods(@PathVariable("skuId") Long skuId);

    /**
     * 商品热度
     * @param skuId
     */
    @GetMapping("/api/list/inner/incrHotScore/{skuId}")
    Result incrHotScore(@PathVariable("skuId") Long skuId);

    /**
     * 获取es中的商品数据，实现页面渲染
     */
    @RequestMapping("/api/list/list")
    Result<Map> list(SearchParam searchParam);
}
