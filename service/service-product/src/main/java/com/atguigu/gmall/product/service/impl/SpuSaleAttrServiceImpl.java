package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SpuSaleAttrServiceImpl
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-02
 * @Description:
 */
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
        implements SpuSaleAttrService {

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    /**
     * 功能：点击销售属性值的组合，跳转其他spu页面
     * 实现：
     * 1. 前端传入skuId
     * 2. 后端 集合（key=[属性值1|属性值2|属性值3]，value=[skuId]）
     *
     * @param spuId
     */
    @Override
    public Map getSkuValueIdsMap(Long spuId) {
        List<Map> list = spuSaleAttrMapper.selectSkuValueIdsMap(spuId);

        // 封装到map集合中
        Map<Object, Object> map = new HashMap<>();
        if (list != null && list.size() > 0) {
            for (Map m : list) {
                map.put(m.get("value_ids"),m.get("sku_id"));
            }
        }

        return map;

    }
}
