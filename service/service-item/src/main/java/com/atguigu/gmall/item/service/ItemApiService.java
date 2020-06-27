package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * ItemApiService
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-06
 * @Description:
 */
public interface ItemApiService {

    /**
     * 获取sku信息，价格，分级信息，销售属性.封装成map集合
     */
    Map<String, Object> getItem(Long skuId);
}