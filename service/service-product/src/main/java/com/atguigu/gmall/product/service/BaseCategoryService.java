package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;

import java.util.List;

/**
 * BaseCategoryService
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-01
 * @Description:
 */
public interface BaseCategoryService {

    /**
     * 查询所有一级分类
     */
    List<BaseCategory1> getCategory1();

    /**
     * 查询所有三级分类
     * @param category1Id 一级分类id
     * @return
     */
    List<BaseCategory2> getCategory2(Long category1Id);

    /**
     * 查询所有二级分类
     * @param category2Id 二级分类
     * @return
     */
    List<BaseCategory3> getCategory3(Long category2Id);

    /**
     * 根据三级分类id查询分类信息
     */
    BaseCategoryView getCategoryView(Long category3Id);

    /**
     * 首页查询
     */
    List<JSONObject> getBaseCategoryList();
}