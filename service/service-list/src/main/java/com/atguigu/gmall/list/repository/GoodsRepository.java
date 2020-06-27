package com.atguigu.gmall.list.repository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * GoodsRepository
 *
 * @Author: simplesilent
 * @CreateTime: 2020-06-16
 * @Description:
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long>{
}