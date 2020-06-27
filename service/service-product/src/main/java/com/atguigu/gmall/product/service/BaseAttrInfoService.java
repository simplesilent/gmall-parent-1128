package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;

import java.util.List;

/**
 * BaseAttrInfoService
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-01
 * @Description:
 */
public interface BaseAttrInfoService {

    /**
     * 获取分类id获取平台属性
     */
    List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 编辑平台属性
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据属性id获取属性值信息
     * @param attrId
     * @return
     */
    List<BaseAttrValue> getAttrValueList(Long attrId);

    /**
     * 根据skuId查询对应平台属性值
     */
    List<SearchAttr> getAttrList(Long skuId);
}