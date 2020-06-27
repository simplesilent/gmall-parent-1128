package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.repository.GoodsRepository;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import javassist.expr.Cast;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SearchServiceImpl
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-16
 * @Description:
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    // ===================================================================
    // =============================查询ES中数据===========================
    // ===================================================================
    /**
     * 获取es中的数据，封装到SearchResponseVo
     */
    @Override
    public SearchResponseVo list(SearchParam searchParam) {

        // 调用封装Dsl语句的方法
        SearchRequest searchRequest = this.buildQueryDSL(searchParam);

        SearchResponse searchResponse = null;
        try {
            // 调用es
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 解析返回结果
        SearchResponseVo searchResponseVo = this.parseSearchResult(searchResponse);

        return searchResponseVo;
    }

    /**
     * 调用封装Dsl语句的方法
     */
    private SearchRequest buildQueryDSL(SearchParam searchParam) {

        // =================================获取参数===============================
        // 分页参数
        Integer pageNo = searchParam.getPageNo();
        Integer pageSize = searchParam.getPageSize();
        // 分类信息
        Long category1Id = searchParam.getCategory1Id();
        Long category2Id = searchParam.getCategory2Id();
        Long category3Id = searchParam.getCategory3Id();
        // 品牌信息
        String trademark = searchParam.getTrademark();
        // 搜索关键字
        String keyword = searchParam.getKeyword();
        // 排序
        String order = searchParam.getOrder();
        // 平台属性
        String[] props = searchParam.getProps();

        // ==================================搜索条件==============================
        // 总的{}
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // bool复合查询 "bool":{}
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // 1. 设置关键字查询
        if (StringUtils.isNotBlank(keyword)) {
            // 设置关键字高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title");
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);

            boolQueryBuilder.must(new MatchQueryBuilder("title", keyword));
        }

        // 2. 设置分类查询  --- 过滤查询
        if (null != category1Id) {
            boolQueryBuilder.filter(new TermQueryBuilder("category1Id", category1Id));
        }
        if (null != category2Id) {
            boolQueryBuilder.filter(new TermQueryBuilder("category2Id", category2Id));
        }
        if (null != category3Id) {
            boolQueryBuilder.filter(new TermQueryBuilder("category3Id", category3Id));
        }

        // 3. 分页查询
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(60);

        // 4.排序
        // 后台拼接：1:hotScore 2:price  前台页面传递：order=2:desc
        if (!Objects.equals(null, order)) {
            String[] split = order.split(":");
            if (split != null && split.length == 2) {
                // 获取参数
                String type = split[0];
                String sort = split[1];

                switch (type) {
                    case "1":
                        searchSourceBuilder.sort("hotScore", "asc" == sort ? SortOrder.ASC : SortOrder.DESC);
                        break;
                    case "2":
                        searchSourceBuilder.sort("price", "asc" == sort ? SortOrder.ASC : SortOrder.DESC);
                        break;
                    default:
                        break;
                }
            }
        }

        // 5. 属性条件 前台数据：【attrId:attrValue:attrName】 --->  23:4G:运行内存
        // 嵌套查询
        if (null != props && props.length > 0) {
            for (String prop : props) {
                // 将 prop 分割
                String[] split = prop.split(":");
                if (null != split && split.length == 3) {
                    String attrId = split[0]; // 属性id
                    String attrValue = split[1]; // 属性值名称
                    String attrName = split[2]; // 属性名称

                    // 最内层查询
                    BoolQueryBuilder subBoolQueryBuilder = new BoolQueryBuilder();
                    subBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                    subBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrValue", attrValue));
                    // subBoolQueryBuilder.must(QueryBuilders.termQuery("attrName",attrName));

                    // 外层查询
                    BoolQueryBuilder boolQueryBuilderProp = new BoolQueryBuilder();
                    boolQueryBuilderProp.must(new NestedQueryBuilder("attrs", subBoolQueryBuilder, ScoreMode.None));

                    // 封装到外层层查询
                    boolQueryBuilder.filter(boolQueryBuilderProp);
                }
            }
        }

        // 6.品牌过滤
        // trademark=2:华为  tmId:tmName
        if (!StringUtils.isEmpty(trademark)) {
            String[] split = trademark.split(":");
            if (split != null && split.length == 2) {
                String tmId = split[0];
                String tmName = split[1];
                // 根据品牌id查询
                BoolQueryBuilder boolQueryBuilderTrademark = new BoolQueryBuilder();
                boolQueryBuilderTrademark.must(QueryBuilders.termQuery("tmName", tmName));
                boolQueryBuilder.filter(boolQueryBuilderTrademark);
            }
        }

        // 商标聚合
        TermsAggregationBuilder termsAggregationBuilderTradeMark = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));

        // 平台属性聚合
        NestedAggregationBuilder nestedAggregationBuilderProp = AggregationBuilders.nested("attrsAgg", "attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")));


        // 将复合搜索放到{}中
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(termsAggregationBuilderTradeMark);
        searchSourceBuilder.aggregation(nestedAggregationBuilderProp);


        // ================================================================================
        // 设置搜索库和表的请求
        SearchRequest searchRequest = new SearchRequest("goods");
        searchRequest.types("info");
        // 加入搜索的条件
        searchRequest.source(searchSourceBuilder);
        log.info(searchSourceBuilder.toString());


        return searchRequest;
    }

    /**
     * 解析返回结果
     */
    private SearchResponseVo parseSearchResult(SearchResponse searchResponse) {

        // 封装到 最终返回结果SearchResponseVo
        SearchResponseVo searchResponseVo = new SearchResponseVo();

        // 获取查询结果 {... hits:{hits:{ [{}],[{}],[{}] } },
        //                  aggregations:{
        //                      tmIdAgg:{},attrsAgg:{} },
        //              ...}

        // ==============================1. 封装goodList==========================================
        // 获取hits中的对象数据
        // log.info(searchResponse.toString());
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<Goods> goodsList = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 获取hits中的_source中的内容
            String sourceAsString = hit.getSourceAsString();
            //log.info(sourceAsString);
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);

            // 为关键字设置高亮
            HighlightField highlightTitle = hit.getHighlightFields().get("title");
            // 用高亮关键字替换原来的关键字
            if (highlightTitle != null) {
                Text[] fragments = highlightTitle.getFragments();
                goods.setTitle(fragments[0].toString());
            }

            goodsList.add(goods);
        }
        searchResponseVo.setGoodsList(goodsList);

        // ==============================2. 封装trademarkList========================================
        // 解析品牌的聚合结果
        // Aggregations Map<String,Aggregation>
        Aggregations aggregations = searchResponse.getAggregations();
        // asMap().get(name);  <=====>  get(name)
        //Aggregation tmIdAgg = aggregations.asMap().get("tmIdAgg");
        ParsedLongTerms tmIdAgg = aggregations.get("tmIdAgg");
        long tmId = tmIdAgg.getBuckets().get(0).getKeyAsNumber().longValue();

        // 将结果封装到 SearchResponseTmVo 的集合中
        List<SearchResponseTmVo> searchResponseTmVoList = new ArrayList<>();
        // 封装 品牌名称 和 品牌图片地址
        List<? extends Terms.Bucket> buckets = tmIdAgg.getBuckets();
        buckets.stream().map(bucket -> {
            // 创建一个SearchResponseTmVo类
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            searchResponseTmVo.setTmId(tmId);

            // 取出数据 -- 品牌名称
            ParsedStringTerms tmNameAgg = ((Terms.Bucket) bucket).getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseTmVo.setTmName(tmName);
            // 取出数据 -- 取出品牌图片地址
            ParsedStringTerms tmLogoUrlAgg = ((Terms.Bucket) bucket).getAggregations().get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            searchResponseTmVo.setTmLogoUrl(tmLogoUrl);

            return searchResponseTmVoList.add(searchResponseTmVo);
        }).collect(Collectors.toList());

        // 放入返回对象
        searchResponseVo.setTrademarkList(searchResponseTmVoList);

        // ===============================3. 封装attrsList============================================
        // 解析属性聚合结果
        // attrsAgg
        // |----attrIdAgg
        // |________attrNameAgg
        // |________attrValueAgg
        ParsedNested attrsAgg = aggregations.get("attrsAgg");
        Aggregations attrsAggAggregations = attrsAgg.getAggregations();


        // 将属性封装到 SearchResponseAttrVo 集合中
        List<SearchResponseAttrVo> attrsList = new ArrayList<>();

        ParsedLongTerms attrIdAgg = attrsAggAggregations.get("attrIdAgg");
        attrIdAgg.getBuckets().stream().map(attrIdbuckets -> {

            // 创建SearchResponseAttrVo对象
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();

            // 获取到属性id，封装
            long attrId = ((Terms.Bucket) attrIdbuckets).getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);

            // 解析 属性聚合结果 ，封装
            ParsedStringTerms attrNameAgg = ((Terms.Bucket) attrIdbuckets).getAggregations().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseAttrVo.setAttrName(attrName);

            // 解析 属性值聚合封装
            List<String> attrValueList = new ArrayList<>();
            ParsedStringTerms attrValueAgg = ((Terms.Bucket) attrIdbuckets).getAggregations().get("attrValueAgg");
            attrValueAgg.getBuckets().stream().map(bucket -> {
                String attrValue = ((Terms.Bucket) bucket).getKeyAsString();
                return attrValueList.add(attrValue);
            }).collect(Collectors.toList());
            searchResponseAttrVo.setAttrValueList(attrValueList);

            return attrsList.add(searchResponseAttrVo);
        }).collect(Collectors.toList());

        searchResponseVo.setAttrsList(attrsList);


        // 返回最终结果
        return searchResponseVo;

    }


    // ===================================================================
    // =============================商品上下架=============================
    // ===================================================================

    /**
     * 商品上架，查询数据[skuInfo，分类信息，平台属性，品牌]并将数据封装到Goods中
     *
     * @param skuId
     */
    @Override
    public void upperGoods(Long skuId) {
        Goods goods = new Goods();

        // 1.查询skuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        if (skuInfo != null) {

            goods.setId(skuId);
            goods.setDefaultImg(skuInfo.getSkuDefaultImg());
            goods.setTitle(skuInfo.getSkuName());
            goods.setPrice(skuInfo.getPrice().doubleValue());
            goods.setCreateTime(new Date());

            // 2.查询分类信息
            BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            if (baseCategoryView != null) {
                goods.setCategory1Id(baseCategoryView.getCategory1Id());
                goods.setCategory1Name(baseCategoryView.getCategory1Name());
                goods.setCategory2Id(baseCategoryView.getCategory2Id());
                goods.setCategory2Name(baseCategoryView.getCategory2Name());
                goods.setCategory3Id(baseCategoryView.getCategory3Id());
                goods.setCategory3Name(baseCategoryView.getCategory3Name());
            }

            // 3.查询品牌
            BaseTrademark baseTrademark = productFeignClient.getBaseTrademark(skuInfo.getTmId());
            if (baseTrademark != null) {
                goods.setTmId(baseTrademark.getId());
                goods.setTmName(baseTrademark.getTmName());
                goods.setTmLogoUrl(baseTrademark.getLogoUrl());
            }

            // 4.查询skuInfo对应平台属性
            List<SearchAttr> searchAttrList = productFeignClient.getAttrList(skuId);
            if (searchAttrList.size() > 0 && searchAttrList != null) {
                goods.setAttrs(searchAttrList);
            }

            goodsRepository.save(goods);
        }
    }

    /**
     * 商品下架
     *
     * @param skuId
     */
    @Override
    public void lowerGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    /**
     * 商品热度
     *
     * @param skuId
     */
    @Override
    public void incrHotScore(Long skuId) {
        // 利用Redis的中 Zset 实现商品热度排名
        String hotKey = "hotscore";
        Double hotscore = redisTemplate.opsForZSet().incrementScore(hotKey, "skuId:" + skuId, 1);

        // 当每增加10个时，更新es
        if (hotscore % 10 == 0) {
            // findById：返回实体
            Optional<Goods> optional = goodsRepository.findById(skuId);
            log.info(optional.toString());
            Goods goods = optional.get();
            goods.setHotScore(Math.round(hotscore));
            goodsRepository.save(goods);
        }
    }

}
