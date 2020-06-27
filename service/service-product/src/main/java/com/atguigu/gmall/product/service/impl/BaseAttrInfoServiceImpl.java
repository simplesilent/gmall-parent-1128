package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BaseAttrInfoServiceImpl
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-01
 * @Description:
 */
@Service
public class BaseAttrInfoServiceImpl implements BaseAttrInfoService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    /**
     * 获取分类id获取平台属性
     *
     * @param category1Id 一级分类id
     * @param category2Id 二级分类id
     * @param category3Id 三级分类id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {

        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.getAttrInfoList(category1Id, category2Id, category3Id);
        return baseAttrInfoList;
    }


    /**
     * 根据属性id获取属性值信息
     *
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_id", attrId);
        return baseAttrValueMapper.selectList(wrapper);
    }

    /**
     * 编辑平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 根据平台属性回去平台id
        Long attrId = baseAttrInfo.getId();

        if (attrId != null) {
            // 修改
            baseAttrInfoMapper.updateById(baseAttrInfo);
            // 先删除属性值，再重新添加
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id", attrId);
            baseAttrValueMapper.delete(wrapper);
        } else {
            // 添加
            baseAttrInfoMapper.insert(baseAttrInfo);
            // 添加完成，获取id
            attrId = baseAttrInfo.getId();
        }

        // 获取页面传递过来属性值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if (attrValueList.size() > 0 && attrValueList != null) {
            // 遍历，并添加
            for (BaseAttrValue baseAttrValue : attrValueList) {
                baseAttrValue.setAttrId(attrId);
                baseAttrValueMapper.insert(baseAttrValue);
            }

        }
    }

    /**
     * 根据skuId查询对应平台属性值
     *
     * @param skuId
     */
    @Override
    public List<SearchAttr> getAttrList(Long skuId) {
        return baseAttrInfoMapper.selectAttrList(skuId);
    }
}
