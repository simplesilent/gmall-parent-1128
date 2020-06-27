package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.BaseCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * BaseCategory1ServiceImpl
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-01
 * @Description:
 */
@Service
public class BaseCategoryServiceImpl implements BaseCategoryService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    /**
     * 查询所有一级分类
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 查询所有二级分类
     * @param category1Id 一级分类id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        QueryWrapper<BaseCategory2> wrapper = new QueryWrapper<>();
        wrapper.eq("category1_id", category1Id);
        return baseCategory2Mapper.selectList(wrapper);
    }

    /**
     * 查询所有三级分类
     *
     * @param category2Id 二级分类
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        QueryWrapper<BaseCategory3> wrapper = new QueryWrapper<>();
        wrapper.eq("category2_id", category2Id);
        return baseCategory3Mapper.selectList(wrapper);
    }

    /**
     * 根据三级分类id查询分类信息
     *
     * @param category3Id
     */
    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    /**
     * 首页查询
     * @return
     */
    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);

        // 查询并封装页面需要的集合
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViews.stream()
                .collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));

        // 可以理解按照一级分类id归类的集合
        Set<Map.Entry<Long, List<BaseCategoryView>>> entries1 = category1Map.entrySet();
        int index = 1;
        // 遍历，封装
        List<JSONObject> list = new ArrayList<>();
        for (Map.Entry<Long, List<BaseCategoryView>> entry1 : entries1) {
            Long category1Id = entry1.getKey();
            List<BaseCategoryView> category1List1 = entry1.getValue();

            // 一级
            JSONObject category1 = new JSONObject();
            category1.put("index", index);
            category1.put("categoryId", category1Id);
            category1.put("categoryName", category1List1.get(0).getCategory1Name());

            // 封装二级分类
            Map<Long, List<BaseCategoryView>> category2Map = category1List1.stream()
                    .collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));

            Set<Map.Entry<Long, List<BaseCategoryView>>> entries2 = category2Map.entrySet();
            List<JSONObject> category2Child = new ArrayList<>();
            for (Map.Entry<Long, List<BaseCategoryView>> entry2 : entries2) {
                Long category2Id = entry2.getKey();
                List<BaseCategoryView> category2List1 = entry2.getValue();

                // 二级
                JSONObject category2 = new JSONObject();
                category2.put("categoryId", category2Id);
                category2.put("categoryName", category2List1.get(0).getCategory2Name());
                // 封装二级到一级的子集中
                category2Child.add(category2);

                // 封装三级到二级中
                List<JSONObject> category3Child = new ArrayList<>();
                category2List1.stream().forEach(entry3 -> {
                    JSONObject category3 = new JSONObject();
                    category3.put("categoryId", entry3.getCategory3Id());
                    category3.put("categoryName", entry3.getCategory3Name());
                    category3Child.add(category3);
                });

                // 将三级数据放到二级分类中
                category2.put("categoryChild", category3Child);
            }

            // 将二级分类放到一级中
            category1.put("categoryChild", category2Child);
            list.add(category1);
            index++;
        }
        return list;
    }
}
