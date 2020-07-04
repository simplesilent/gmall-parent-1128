package com.atguigu.gmall.product.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ProductApiController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-05
 * @Description:
 */
@RestController
@RequestMapping("/api/product")
public class ProductApiController {

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private BaseCategoryService baseCategoryService;

    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    private SpuSaleAttrService spuSaleAttrService;

    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @Autowired
    private BaseAttrInfoService baseAttrInfoService;

    /**
     * 根据skuId查询skuInfo
     */
    @GetMapping("/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId) {
        return skuInfoService.getSkuInfo(skuId);
    }

    /**
     * 根据skuId查询价格
     */
    @GetMapping("/inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId) {
        return skuInfoService.getSkuPrice(skuId);
    }

    /**
     * 根据三级分类id查询分类信息
     * BaseCategoryView：封装了一、二、三级分类编号和分类名称
     */
    @GetMapping("/inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id) {
        return baseCategoryService.getCategoryView(category3Id);
    }

    /**
     * 根据spuId和skuId查询销售属性，并且添加一个字段isChecked（用来表示被选中的销售属性值）
     */
    @GetMapping("/inner/getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(
            @PathVariable("spuId") Long spuId, @PathVariable("skuId") Long skuId) {

        return spuInfoService.getSpuSaleAttrListCheckBySku(spuId, skuId);
    }

    /**
     * 功能：点击销售属性值的组合，跳转其他spu页面
     * 实现：
     * 1. 前端传入skuId
     * 2. 后端 集合（key=[属性值1|属性值2|属性值3]，value=[skuId]）
     */
    @GetMapping("/inner/getSkuValueIdsMap/{skuId}")
    public Map getSkuValueIdsMap(@PathVariable("skuId") Long skuId) {
        return spuSaleAttrService.getSkuValueIdsMap(skuId);
    }

    /**
     * 首页查询
     */
    @GetMapping("/getBaseCategoryList")
    public Result getBaseCategoryList() {
        List<JSONObject> list = baseCategoryService.getBaseCategoryList();
        return Result.ok(list);
    }

    /**
     * 查询品牌
     */
    @GetMapping("/inner/getBaseTrademark/{tmId}")
    public BaseTrademark getBaseTrademark(@PathVariable("tmId") Long tmId) {
        return baseTrademarkService.getById(tmId);
    }

    /**
     * 根据skuId查询对应平台属性值
     */
    @GetMapping("/inner/getAttrList/{skuId}")
    List<SearchAttr> getAttrList(@PathVariable("skuId") Long skuId) {
        return baseAttrInfoService.getAttrList(skuId);
    }

}
