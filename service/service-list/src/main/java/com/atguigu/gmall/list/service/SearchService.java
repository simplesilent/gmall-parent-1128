package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;

/**
 * SearchService
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-16
 * @Description:
 */
public interface SearchService {

    void upperGoods(Long skuId);

    void lowerGoods(Long skuId);

    /**
     * 商品热度
     * @param skuId
     */
    void incrHotScore(Long skuId);

    /**
     * 获取es中的数据，封装到SearchResponseVo
     * @param searchParam 查询条件
     * @return
     */
    SearchResponseVo list(SearchParam searchParam);
}