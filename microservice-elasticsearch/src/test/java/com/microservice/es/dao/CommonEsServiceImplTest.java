package com.anqi.es.highclient;

import com.alibaba.fastjson.JSON;
import com.anqi.es.BaseJunitTest;
import com.anqi.es.DemoEsApplication;
import com.anqi.es.entity.UserCollection;
import com.anqi.es.service.CommonEsService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;


public class CommonEsServiceImplTest extends BaseJunitTest {

    private String index = "date_test_id";
    @Autowired
    CommonEsService commonEsService;

    @Autowired
    private RestHighLevelClient client;
    @Test
    public void add() {
        for (int i=0;i<5;i++){
            UserCollection userCollection=new UserCollection();
            userCollection.setBrowserCount(12L);
            userCollection.setTableName("ldw"+i);
            userCollection.setUpdatedTime(new Date());
            commonEsService.add(index, "1212",JSON.toJSONString(userCollection),10);
        }
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void updateDoc() {
    }

    @Test
    public void findByMap() {
        System.out.println(commonEsService.findByMap(index,new HashMap<>()));
    }

    @Test
    public void findByMapAndRange() {
    }

    @Test
    public void findByMap1() {

        List list= commonEsService.findByMap(index, new HashMap<>());
        System.out.println(JSON.toJSON(list));
    }

    @Test
    public void findByMap2() {
    }

    @Test
    public void findByMap3() {
    }

    @Test
    public void createIndex() {
        commonEsService.createIndex("date_test_id");
    }

    @Test
    public void sumGroupByField() {
    }

    @Test
    public void batchAdd() {
        List list=new ArrayList();
        for (int i=0;i<5;i++){
            UserCollection userCollection=new UserCollection();
            userCollection.setBrowserCount(12L);
            userCollection.setTableName("111aaa"+i);
            userCollection.setUpdatedTime(new Date());
            list.add(userCollection);
        }
        commonEsService.batchAdd(index,list   );
    }


    @Test
    public void wildcardSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.wildcardQuery("tableName", "*" + "adaa" + "*"));
        searchSourceBuilder.query(QueryBuilders.boolQuery().must(boolQueryBuilder));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        System.out.println(searchHits.length);
        for (SearchHit hit : searchHits) {
            printResult(hit);

        }
    }


}