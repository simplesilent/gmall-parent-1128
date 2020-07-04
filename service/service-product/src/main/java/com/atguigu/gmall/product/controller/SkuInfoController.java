package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SkuInfoController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-03
 * @Description:
 */
@Api(tags = "商品SKU管理")
@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService;

    @ApiOperation("根据SPU的id获取销售属性集合")
    @GetMapping("/spuSaleAttrList/{spuInfoId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrList(@PathVariable("spuInfoId") Long spuInfoId) {
        List<SpuSaleAttr> spuSaleAttrList = skuInfoService.getSpuSaleAttrList(spuInfoId);
        return Result.ok(spuSaleAttrList);
    }

    @ApiOperation("根据SPU的id获取图片集合")
    @GetMapping("/spuImageList/{spuInfoId}")
    public Result<List<SpuImage>> getSpuImageList(@PathVariable("spuInfoId") Long spuInfoId) {
        List<SpuImage> spuImageList = skuInfoService.getSpuImageList(spuInfoId);
        return Result.ok(spuImageList);
    }

    @ApiOperation("添加SKU")
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @ApiOperation("获取SKU集合")
    @GetMapping("/list/{page}/{size}")
    public Result<IPage<SkuInfo>> getSkuInfoPage(@PathVariable("page") Long page, @PathVariable("size") Long size) {
        Page<SkuInfo> skuInfoPage = new Page<>(page, size);
        IPage<SkuInfo> skuInfoIPage = skuInfoService.getSkuInfoPage(skuInfoPage);
        return Result.ok(skuInfoIPage);
    }

    @ApiOperation("商品SKU上架")
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId) {
        skuInfoService.onSale(skuId);
        return Result.ok();
    }

    @ApiOperation("商品SKU下架")
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId) {
        skuInfoService.cancelSale(skuId);
        return Result.ok();
    }

}
