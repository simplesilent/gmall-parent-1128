package com.atguigu.gmall.all.service;

import com.atguigu.gmall.common.result.Result;

import java.util.Map;

/**
 * ItemService
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-06
 * @Description:
 */
public interface ItemService {

    /**
     * 获取结果集
     */
    Result<Map> getItem(Long skuId);
}