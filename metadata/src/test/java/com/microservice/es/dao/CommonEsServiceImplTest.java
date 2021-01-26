package com.microservice.es.dao;

import com.alibaba.fastjson.JSON;
import com.ldw.metadata.collector.BaseTest;
import com.ldw.metadata.constant.CommonConstants;
import com.ldw.metadata.service.CommonEsService;
import com.ldw.metadata.vo.CommonSearchVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonEsServiceImplTest extends BaseTest {

    private String index = "date_test_id";
    @Autowired
    CommonEsService commonEsService;

    @Test
    public void add() {
    }

    @Test
    public void update() {
    }

    @Test
    public void update1() {
    }

    @Test
    public void updateByQuery() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void deleteByQuery() {
    }

    @Test
    public void searchIndex() {
        Map<String, Object> mustWhere = new HashMap<String, Object>();
        Map<String, Object> shouldWhere = new HashMap<String, Object>();
        shouldWhere.put("tableName", "1a");
        shouldWhere.put("tableComment", "s打");
        mustWhere.put("should-1", shouldWhere);





        String[] includeFields = {"id", "projectId", "tableName", "tableComment", "dataSourceName", "createdBy", "dataSourceType", "dataSourceId",
                "createdTime", "updatedTime", "relType"};
        Map<String, Object> data = commonEsService.searchIndex(index, 1, 50, mustWhere, null, null, includeFields, null, 30L, true);
        List content= (List) data.get("content");

        Long totalElements= (Long) data.get("totalElements");
        Long totalPages= (Long) data.get("totalPages");

        System.out.println("totalElements:"+totalElements);
        System.out.println("totalPages:"+totalPages);

        content.forEach(list->{
            System.out.println(JSON.toJSONString(list));
        });
    }

    @Test
    public void updateDoc() {
    }

    @Test
    public void findByMap() {
        List list = commonEsService.findByMap(index, new HashMap<>());
        System.out.println(JSON.toJSON(list));
    }

    @Test
    public void findByMapAndRange() {
    }

    @Test
    public void findByMap1() {
    }

    @Test
    public void findByMap2() {
    }

    @Test
    public void findByMap3() {
    }

    @Test
    public void findByMap4() {
    }

    @Test
    public void findByMap5() {
    }

    @Test
    public void createIndex() {
    }

    @Test
    public void sumGroupByField() {
    }

    @Test
    public void findById() {
    }

    @Test
    public void findById1() {
    }

    @Override
    public void init() {

    }
}