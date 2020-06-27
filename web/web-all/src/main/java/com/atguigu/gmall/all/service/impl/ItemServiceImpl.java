package com.atguigu.gmall.all.service.impl;

import com.atguigu.gmall.all.service.ItemService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * ItemServiceImpl
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-06
 * @Description:
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemFeignClient itemFeignClient;

    /**
     * 获取结果集
     *
     * @param skuId
     */
    @Override
    public Result<Map> getItem(Long skuId) {
        return itemFeignClient.getItem(skuId);
    }
}
