package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.all.service.ItemService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * ItemController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-06
 * @Description:
 */
@Controller
@RequestMapping
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * sku详情页（sku信息，价格，分级信息，销售属性）
     */
    @RequestMapping("{skuId}.html")
    public String getItem(@PathVariable("skuId") Long skuId, Model model) {
        Result<Map> result = itemService.getItem(skuId);
        model.addAllAttributes(result.getData());
        return "item/index";
    }

}
