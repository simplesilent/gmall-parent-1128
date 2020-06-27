package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import java.util.List;

/**
 * BaseTrademarkController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-02
 * @Description:
 */
@Api(tags = "商标品牌管理")
@RestController
@RequestMapping("/admin/product/baseTrademark")
@CrossOrigin
public class BaseTrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @ApiOperation("获取商标品牌所有")
    @GetMapping("/getTrademarkList")
    public Result<List<BaseTrademark>> list() {
        List<BaseTrademark> trademarkList = baseTrademarkService.list(null);
        return Result.ok(trademarkList);
    }

    @ApiOperation("获取商标品牌分页列表")
    @GetMapping("/{page}/{size}")
    public Result<IPage<BaseTrademark>> index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable("page") Long page,
            @ApiParam(name = "size", value = "每页显示大小", required = true)
            @PathVariable("size") Long size) {

        Page<BaseTrademark> baseTrademarkPage = new Page<>(page,size);
        IPage<BaseTrademark> baseTrademarkIPage = baseTrademarkService.page(baseTrademarkPage, null);
        return Result.ok(baseTrademarkIPage);
    }

    @ApiOperation("根据id获取商标品牌")
    @GetMapping("/get/{id}")
    public Result<BaseTrademark> getbaseTrademark(@PathVariable("id") Long id) {
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }

    @ApiOperation("添加商标品牌")
    @PostMapping("/save")
    public Result save(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    @ApiOperation("修改商标品牌")
    @PutMapping("/update")
    public Result update(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    @ApiOperation("根据id获取商标品牌")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable("id") Long id) {
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

}
