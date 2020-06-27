package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * SpuSaleAttrService
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-02
 * @Description:
 */
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    /**
     * 功能：点击销售属性值的组合，跳转其他spu页面
     * 实现：
     *      1. 前端传入skuId
     *      2. 后端 集合（key=[属性值1|属性值2|属性值3]，value=[skuId]）
     */
    Map getSkuValueIdsMap(Long spuId);
}