package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BaseCategoryController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-01
 * @Description:
 */
@Api(tags = "商品分类接口")
@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class BaseCategoryController {

    @Autowired
    private BaseCategoryService baseCategoryService;

    @ApiOperation("查询所有一级分类")
    @GetMapping("/getCategory1")
    public Result<List<BaseCategory1>> getCategory1() {
        List<BaseCategory1> baseCategory1List = baseCategoryService.getCategory1();
        return Result.ok(baseCategory1List);
    }

    @ApiOperation("查询所有二级分类")
    @GetMapping("/getCategory2/{category1Id}")
    public Result<List<BaseCategory2>> getCategory2(@PathVariable("category1Id") Long category1Id) {
        List<BaseCategory2> baseCategory2List = baseCategoryService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }

    @ApiOperation("查询所有三级分类")
    @GetMapping("/getCategory3/{category2Id}")
    public Result<List<BaseCategory3>> getCategory3(@PathVariable("category2Id") Long category2Id) {
        List<BaseCategory3> baseCategory3List = baseCategoryService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }

}

