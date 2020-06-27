package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * SkuInfoService
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-03
 * @Description:
 */
public interface SkuInfoService {

    /**
     * 根据SPU的id获取销售属性集合
     * @param spuInfoId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(Long spuInfoId);

    /**
     * 根据SPU的id获取图片集合
     * @param spuInfoId
     * @return
     */
    List<SpuImage> getSpuImageList(Long spuInfoId);

    /**
     * 添加SKU
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 获取SKU集合
     * @param skuInfoPage
     * @return
     */
    IPage<SkuInfo> getSkuInfoPage(Page<SkuInfo> skuInfoPage);

    /**
     * 商品SKU上架
     * @param skuId
     */
    void onSale(Long skuId);

    /**
     * 商品SKU下架
     * @param skuId
     */
    void cancelSale(Long skuId);

    /**
     * 根据skuId查询skuInfo
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 根据skuId查询价格
     */
    BigDecimal getSkuPrice(Long skuId);
}