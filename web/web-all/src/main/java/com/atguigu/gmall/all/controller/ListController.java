package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.SearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * ListController
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-15
 * @Description:
 */
@Controller
public class ListController {

    @Autowired
    private ListFeignClient listFeignClient;

    @RequestMapping({"search.html","list.html"})
    public String list(SearchParam searchParam, Model model) {
        // 调用service-list服务，获取es中的商品数据，实现页面渲染
        Result<Map> result = listFeignClient.list(searchParam);
        model.addAllAttributes(result.getData());
        model.addAttribute("searchParam",searchParam);
        // 拼接urlParam
        String urlParam = makeUrlParam(searchParam);
        model.addAttribute("urlParam",urlParam);

        // 品牌面包屑
        String trademark = searchParam.getTrademark();
        if (StringUtils.isNotBlank(trademark)) {
            String[] split = trademark.split(":");
            model.addAttribute("trademarkParam", split[1]);
        }

        // 属性面包屑
        String[] props = searchParam.getProps();
        if (props != null && props.length > 0) {
            List<Map> attrListCrumb = new ArrayList<>();
            for (String prop : props) {
                String[] split = prop.split(":");

                Map<String, String> map = new HashMap<>();
                map.put("attrId", split[0]);
                map.put("attrValue", split[1]);
                map.put("attrName", split[2]);

                attrListCrumb.add(map);
            }
            model.addAttribute("propsParamList", attrListCrumb);
        }

        // 排序
        String order = searchParam.getOrder();
        if (!Objects.equals(null, order)) {
            String[] split = order.split(":");
            if (split != null && split.length == 2) {
                Map<String, String> map = new HashMap<>(2);
                map.put("type", split[0]);
                map.put("sort", split[1]);
                model.addAttribute("orderMap", map);
            }
        }

        return "list/index";
    }

    /**
     * 拼接urlParam
     */
    private String makeUrlParam(SearchParam searchParam) {
        StringBuilder urlParam = new StringBuilder("search.html?");

        // 判断关键字
        if (searchParam.getKeyword() != null) {
            urlParam.append("keyword=" + searchParam.getKeyword());
        }
       /* // 拼接一级分类
        if (searchParam.getCategory1Id() != null) {
            urlParam.append("category1Id=" + searchParam.getCategory1Id());
        }
        // 拼接二级分类
        if (searchParam.getCategory2Id() != null) {
            urlParam.append("category2Id=" + searchParam.getCategory2Id());
        }*/
        // 拼接三级分类
        if (searchParam.getCategory3Id() != null) {
            urlParam.append("category3Id=" + searchParam.getCategory3Id());
        }
        // 品牌
        if (searchParam.getTrademark() != null) {
            if (urlParam.length() > 0) {
                urlParam.append("&trademark=" + searchParam.getTrademark());
            }
        }
        // 平台属性
        String[] props = searchParam.getProps();
        if (props != null && props.length > 0) {
            for (String prop : props) {
                urlParam.append("&props=" + prop);
            }
        }

        return urlParam.toString();
    }

}
