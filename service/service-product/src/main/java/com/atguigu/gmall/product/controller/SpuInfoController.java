package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * SpuInfoController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-02
 * @Description:
 */
@Api(tags = "商品SPU管理")
@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class SpuInfoController {

    @Autowired
    private SpuInfoService spuInfoService;

    @ApiOperation("获取spu分页列表")
    @GetMapping("/{page}/{size}")
    public Result<IPage<SpuInfo>> index(@ApiParam(name = "page", value = "当前页码", required = true) @PathVariable("page") Long page,
                       @ApiParam(name = "size", value = "每页显示大小", required = true) @PathVariable("size") Long size,
                       @ApiParam(name = "SupInfo", value = "查询对象", required = false) SpuInfo spuInfo) {

        // 分页查询
        Page<SpuInfo> pageInfo = new Page<>(page,size);
        IPage<SpuInfo> spuInfoIPage =  spuInfoService.selectPage(pageInfo,spuInfo);

        return Result.ok(spuInfoIPage);
    }

    @ApiOperation("添加SPU信息")
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        spuInfoService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

}
