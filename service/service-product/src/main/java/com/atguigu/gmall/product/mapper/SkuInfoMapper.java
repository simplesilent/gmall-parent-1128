package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SkuInfoMapper
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-03
 * @Description:
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {


    /**
     * 根据SPU的id获取销售属性集合
     *
     * @param spuInfoId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(Long spuInfoId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@Param("spuId") Long spuId,@Param("skuId") Long skuId);
}