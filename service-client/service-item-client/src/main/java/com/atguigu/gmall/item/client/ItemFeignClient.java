package com.atguigu.gmall.item.client;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ItemFeignClient
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-05
 * @Description:
 */
@FeignClient("service-item")
public interface ItemFeignClient {

    @GetMapping("/api/item/{skuId}")
    public Result getItem(@PathVariable("skuId") Long skuId);
}
