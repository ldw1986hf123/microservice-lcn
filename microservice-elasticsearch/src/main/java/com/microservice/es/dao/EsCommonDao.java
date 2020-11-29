package com.microservice.es.dao;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

public abstract class EsCommonDao<T> {

    private String index = "user_collection";

    private Class<T> clazz;

    @Autowired
    private RestHighLevelClient rhlClient;

    //哪个子类调的这个方法，得到的class就是子类处理的类型（非常重要）
    public EsCommonDao() {
        clazz  = (Class<T>) this.getClass();  //拿到的是子类
        ParameterizedType pt = (ParameterizedType) clazz.getGenericSuperclass();  //BaseDao<Category>
        clazz = (Class) pt.getActualTypeArguments()[0];
        System.out.println(clazz);
    }


    public T getById(Long tableId) {
        T t = null;
        SearchSourceBuilder sourceBuilder;
        sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(200);
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        sourceBuilder.query(boolBuilder);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = rhlClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            System.out.println("总共有" + hits.getHits().length + "条记录");
            if (null != hits) {
                SearchHit searchHit = hits.getAt(0);
                String jsonStr = searchHit.getSourceAsString();
                System.out.println("jsonStr "+ jsonStr);
                t = JSON.parseObject(jsonStr,clazz);
                return t;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }
}
