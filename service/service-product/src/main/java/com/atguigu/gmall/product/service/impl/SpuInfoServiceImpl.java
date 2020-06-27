package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SpuInfoServiceImpl
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-02
 * @Description:
 */
@Service
public class SpuInfoServiceImpl implements SpuInfoService {

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    /**
     * 获取spu分页列表
     *
     * @param pageInfo
     * @param spuInfo
     */
    @Override
    public IPage<SpuInfo> selectPage(Page<SpuInfo> pageInfo, SpuInfo spuInfo) {
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id", spuInfo.getCategory3Id());
        return spuInfoMapper.selectPage(pageInfo, wrapper);
    }

    /**
     * 添加SPU信息
     *
     * @param spuInfo
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {

        // 1. 添加SpuInfo
        spuInfoMapper.insert(spuInfo);
        Long spuInfoId = spuInfo.getId();

        // 2. 添加Spu销售属性
        // 2.1 获取前端Spu销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(spuInfoId);
            spuSaleAttrMapper.insert(spuSaleAttr);

            // 3. 添加Spu销售属性值
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                // 销售属性值这里采用外键关联，使用Spu的id和销售属性的id一同确定销售属性
                spuSaleAttrValue.setSpuId(spuInfoId);
                spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            }

        }

        // 4.  图片上传
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(spuInfoId);
            spuImageMapper.insert(spuImage);
        }
    }

    /**
     * 根据spuId和skuId查询销售属性
     *
     * @param spuId
     * @param skuId
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long spuId, Long skuId) {
        return skuInfoMapper.getSpuSaleAttrListCheckBySku(spuId,skuId);
    }
}
