package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BaseAttrInfoController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-01
 * @Description:
 */
@Api(tags = "平台属性接口")
@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class BaseAttrInfoController {

    @Autowired
    private BaseAttrInfoService baseAttrInfoService;

    @ApiOperation("获取分类id获取平台属性")
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> getAttrInfoList(@PathVariable("category1Id") Long category1Id,
                                                @PathVariable("category2Id") Long category2Id,
                                                @PathVariable("category3Id") Long category3Id) {

        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoService.getAttrInfoList(category1Id, category2Id, category3Id);
        return Result.ok(baseAttrInfoList);
    }

    @ApiOperation("编辑平台属性")
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    @ApiOperation("根据属性id获取属性值信息")
    @GetMapping("/getAttrValueList/{attrId}")
    public Result<List<BaseAttrValue>> getAttrValueList(@PathVariable("attrId") Long attrId) {
        List<BaseAttrValue> baseAttrValueList = baseAttrInfoService.getAttrValueList(attrId);
        return Result.ok(baseAttrValueList);
    }

}
