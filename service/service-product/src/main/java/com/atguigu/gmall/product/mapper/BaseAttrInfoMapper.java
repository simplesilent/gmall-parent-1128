package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * BaseAttrInfoMapper
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-01
 * @Description:
 */
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    List<BaseAttrInfo> getAttrInfoList(@Param("category1Id") Long category1Id,
                                       @Param("category2Id") Long category2Id,
                                       @Param("category3Id") Long category3Id);

    /**
     * 根据skuId查询对应平台属性值
     *
     * @param skuId
     */
    List<SearchAttr> selectAttrList(@Param("skuId") Long skuId);

}