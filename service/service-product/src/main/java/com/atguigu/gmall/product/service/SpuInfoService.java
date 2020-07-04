package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * SpuInfoService
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-02
 * @Description:
 */
public interface SpuInfoService {

    /**
     * 获取spu分页列表
     */
    IPage<SpuInfo> selectPage(Page<SpuInfo> pageInfo, SpuInfo spuInfo);

    /**
     * 添加SPU信息
     *
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 根据spuId和skuId查询销售属性
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long spuId, Long skuId);
}