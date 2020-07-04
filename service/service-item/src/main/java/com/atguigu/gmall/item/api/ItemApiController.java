package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ItemService
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-05
 * @Description:
 */
@RestController
@RequestMapping("/api/item")
public class ItemApiController {


    @Autowired
    private ItemApiService itemApiService;

    /**
     * 获取sku信息，价格，分级信息，销售属性.封装成map集合
     */
    @GetMapping("{skuId}")
    public Result getItem(@PathVariable("skuId") Long skuId) {
        Map<String, Object> map = itemApiService.getItem(skuId);
        return Result.ok(map);
    }

}
