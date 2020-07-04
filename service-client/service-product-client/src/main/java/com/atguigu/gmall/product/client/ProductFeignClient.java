package com.atguigu.gmall.product.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ProductFeignClient
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-05
 * @Description:
 */
@FeignClient(value = "service-product")
public interface ProductFeignClient {


    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);


    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId);


    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id);


    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(
            @PathVariable("spuId") Long spuId, @PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId);

    @GetMapping("/api/product/getBaseCategoryList")
    Result getBaseCategoryList();

    @GetMapping("/api/product/inner/getBaseTrademark/{tmId}")
    BaseTrademark getBaseTrademark(@PathVariable("tmId") Long tmId);

    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    List<SearchAttr> getAttrList(@PathVariable("skuId") Long skuId);
}