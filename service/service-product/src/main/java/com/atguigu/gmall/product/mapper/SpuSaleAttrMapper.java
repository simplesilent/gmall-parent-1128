package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * SpuSaleAttrMapper
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-02
 * @Description:
 */
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * 根据spuId 查询map 集合属性
     * @param spuId
     * @return
     */
    List<Map> selectSkuValueIdsMap(Long spuId);
}