package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ListApiController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-15
 * @Description:
 */
@RestController
@RequestMapping("/api/list")
public class ListApiController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private SearchService searchService;

    @GetMapping("/inner/createIndex")
    public Result createIndex() {
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    @GetMapping("/inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId) {
        searchService.upperGoods(skuId);
        return Result.ok();
    }

    @GetMapping("/inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId) {
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    /**
     * 商品热度
     * @param skuId
     */
    @GetMapping("/inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId) {
        searchService.incrHotScore(skuId);
        return Result.ok();
    }


    /**
     * 获取es中的商品数据，实现页面渲染
     */
    @RequestMapping("/list")
    Result<Map> list(@RequestBody SearchParam searchParam) {

        // 调用SearchServiceImpl，获取es中的数据，封装到SearchResponseVo
        // SearchResponseVo包含的内容：【Goods、SearchResponseAttrVo、SearchResponseTmVo、分页数据】
        SearchResponseVo searchResponseVo = searchService.list(searchParam);

        // 封装到map
        Map<String, Object> map = new HashMap<>(3);
        map.put("trademarkList", searchResponseVo.getTrademarkList());
        map.put("goodsList", searchResponseVo.getGoodsList());
        map.put("attrsList", searchResponseVo.getAttrsList());

        return Result.ok(map);
    }

}
