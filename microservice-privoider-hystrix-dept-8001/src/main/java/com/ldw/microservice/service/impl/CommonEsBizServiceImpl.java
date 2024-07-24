package com.ldw.microservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deepexi.data.metadata.biz.CommonEsBizService;
import com.deepexi.data.metadata.domain.query.es.CommonSearchQuery;
import com.deepexi.data.metadata.util.FieldUtils;
import com.deepexi.util.pageHelper.PageBean;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.util.CollectionUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/2 14:34
 * @Description
 */
@Slf4j
@Service
@Lazy
public class CommonEsBizServiceImpl implements CommonEsBizService {

    @Autowired(required = false)
    private RestHighLevelClient client;

    @Override
    public <T> T findOne(CommonSearchQuery<T> data) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Map<String, Object> mustWhere = data.getMustWhere();
            if (mustWhere != null && !mustWhere.isEmpty()) {
                mustWhere.forEach((k, v) -> {
                    if (v instanceof Map) {
                        Map<String, Date> mapV = (Map<String, Date>) v;
                        if (mapV != null) {
                            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k);
                            if (null != mapV.get("start")) {
                                rangeQueryBuilder.gte(format.format(mapV.get("start")));
                            }
                            if (null != mapV.get("end")) {
                                rangeQueryBuilder.lt(format.format(mapV.get("end")));
                            }
                            boolQueryBuilder.must(rangeQueryBuilder);
                        }
                    } else if (v instanceof Object[]) {
                        boolQueryBuilder.must(QueryBuilders.termsQuery(k, (Object[]) v));
                    } else if (FieldUtils.isNumeric(v + "")) {
                        boolQueryBuilder.must(QueryBuilders.matchQuery(k, v));
                    } else if (FieldUtils.isEnglishLetter(v + "")) {
                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(k, v.toString()));
                    } else {
                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(k + ".keyword", v.toString()));
                    }
                });
            }
            sourceBuilder.query(boolQueryBuilder);
            sourceBuilder.timeout(new TimeValue(data.getTimeout(), TimeUnit.SECONDS));
            if (!CollectionUtils.isEmpty(data.getIncludeFields())
                    || !CollectionUtils.isEmpty(data.getExcludeFields())) {
                sourceBuilder.fetchSource(data.getIncludeFields(), data.getExcludeFields());
            }
            SearchRequest request = new SearchRequest();
            request.indices(data.getIndex());
            request.source(sourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            if (response.status() != RestStatus.OK || response.getHits().getTotalHits().value <= 0) {
                return data.getClassObject().newInstance();
            }
            List<Map<String, Object>> result = dbFieldToObjectField(response.getHits().getHits());
            return JSON.parseObject(JSON.toJSONString(result.get(0)), data.getClassObject());
        } catch (Exception e) {
            log.error("failed to found single entity !", e);
        }
        T responseData = null;
        try {
            responseData = data.getClassObject().newInstance();
        } catch (Exception e) {
            log.error("failed to found single entity !", e);
        }
        return responseData;
    }

    /**
     * 直接获取某个index下的满足条件的doc，
     * 分页返回一个所哟有doc的json字符串集合list
     * 有排序
     *
     * @return
     * @throws IOException
     */
    @Override
    public <T> PageBean<T> findByMap(String index, Map<String, String> mustWhere, Integer page, Integer size, Class<T> clazz, String sortedFiled, String sorted) {
        PageBean<T> resultPageBean = new PageBean<>();
        List<T> resultList = new ArrayList<>(10);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (null != page && null != size) {
            sourceBuilder.from((page - 1) * size);
            sourceBuilder.size(size);
        }

        // 按照id字段情况升序
        if ("ASC".equals(sorted)) {
            sourceBuilder.sort(new FieldSortBuilder(sortedFiled).order(SortOrder.ASC));
        } else {
            sourceBuilder.sort(new FieldSortBuilder(sortedFiled).order(SortOrder.DESC));
        }
        long totalElements = 0L;
        try {
            SearchResponse response = finByMap(index, mustWhere, null, QueryBuilders.boolQuery(), sourceBuilder);
            if (null != response) {
                SearchHits hits = response.getHits();
                totalElements = response.getHits().getTotalHits().value;
                for (SearchHit hit : hits) {
                    T bean = clazz.newInstance();
                    String jsonStr = hit.getSourceAsString();
                    cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
                    bean = JSONUtil.toBean(jsonObject, clazz, true);
                    resultList.add(bean);
                }
            }
        } catch (IllegalAccessException e) {
            log.error("findByMap 异常", e);
        } catch (InstantiationException e) {
            log.error("findByMap 异常", e);
        }

        Page<T> resultPage = new Page<>();
        resultPage.setPageNum(page);
        resultPage.setPageSize(size);
        resultPage.setTotal(totalElements);
        // 设置返回数据信息
        resultPage.addAll(resultList);
        resultPageBean = new PageBean<>(resultPage);
        return resultPageBean;
    }

    @Override
    public <T> PageBean<T> pageIndex(String index, Integer from, Integer size, Map<String, Object> mustWhere, Map<String, Object> shouldWhere, Map<String, Boolean> sortFieldsToAsc, String[] includeFields, String[] excludeFields, Long timeOut, Class<T> tClass) {
        PageBean<T> pageBean = new PageBean<T>();
        SearchHit[] hits = null;
        try {
            // 初始化es查询请求对象
            SearchRequest request = initRequestParameters(index, from, size, mustWhere, shouldWhere, sortFieldsToAsc,
                    includeFields, excludeFields, timeOut, true);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 解析返回
            if (response.status() != RestStatus.OK || response.getHits().getTotalHits().value <= 0) {
                return pageBean;
            }
            hits = response.getHits().getHits();
            long totalElements = response.getHits().getTotalHits().value;
            long totalPages = 0;
            if (totalElements % size == 0) {
                totalPages = totalElements / size;
            } else {
                totalPages = (totalElements / size) + 1;
            }
            pageBean.setTotalElements(totalElements);
            pageBean.setTotalPages(Math.toIntExact(totalPages));

            if (CollectionUtils.isEmpty(hits)) {
                return pageBean;
            }
            List<Map<String, Object>> data = dbFieldToObjectField(hits);
            JSONArray array = JSONArray.parseArray(JSONObject.toJSONString(data));
            List<T> jsonDTOs = array.toJavaList(tClass);
            pageBean.setContent(jsonDTOs);
            return pageBean;
        } catch (Exception e) {
            log.error("分页查询索引异常", e);
        }
        return pageBean;
    }

    @Override
    public <T> List<T> searchIndex(String index, Map<String, Object> mustWhere, Map<String, Object> shouldWhere, Map<String, Boolean> sortFieldsToAsc, String[] includeFields, String[] excludeFields, Long timeOut, Class<T> tClass) {
        List<T> result = new ArrayList<>();
        SearchHit[] hits = null;
        try {
            // 初始化es查询请求对象
            SearchRequest request = initRequestParameters(index, null, null, mustWhere, shouldWhere, sortFieldsToAsc,
                    includeFields, excludeFields, timeOut, false);
            // 请求
//            log.info(request.source().toString());

            List<SearchHit> searchHits = scrollSearchAll(timeOut, request);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(searchHits)) {
                hits = searchHits.toArray(new SearchHit[searchHits.size()]);
            }

            if (CollectionUtils.isEmpty(hits)) {
                return result;
            }
            List<Map<String, Object>> data = dbFieldToObjectField(hits);
            JSONArray array = JSONArray.parseArray(JSONObject.toJSONString(data));
            List<T> jsonDTOs = array.toJavaList(tClass);
            return jsonDTOs;
        } catch (Exception e) {
            log.error("列表查询索引异常", e);
        }
        return null;
    }

    /**
     * @param scrollTimeOut
     * @param searchRequest
     * @return
     * @throws IOException
     * @desc 使用游标获取全部结果，返回SearchHit集合
     */
    @SuppressWarnings("deprecation")
    private List<SearchHit> scrollSearchAll(Long scrollTimeOut, SearchRequest searchRequest) throws IOException {
        Scroll scroll = new Scroll(new TimeValue(scrollTimeOut, TimeUnit.SECONDS));
        searchRequest.scroll(scroll);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<SearchHit> resultSearchHit = new ArrayList<>();
        while (ArrayUtils.isNotEmpty(hits)) {
            for (SearchHit hit : hits) {
                resultSearchHit.add(hit);
            }
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(scroll);
            SearchResponse searchScrollResponse = client.searchScroll(searchScrollRequest, RequestOptions.DEFAULT);
            scrollId = searchScrollResponse.getScrollId();
            hits = searchScrollResponse.getHits().getHits();
        }
        // 及时清除es快照，释放资源
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        return resultSearchHit;
    }


    /**
     * @param hits
     * @return
     * @desc 将es db字段名转换成对象字段名
     */
    private List<Map<String, Object>> dbFieldToObjectField(SearchHit... hits) {
        List<Map<String, Object>> result = Arrays.stream(hits).map(b -> {
            Map<String, Object> sourceMap = b.getSourceAsMap();
            Map<String, Object> targetMap = new HashMap<String, Object>();
            Iterator<Map.Entry<String, Object>> it = sourceMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                if (entry.getKey().indexOf("_") > -1) {
                    String objectFieldName = FieldUtils.dbFieldNameToObjectName(entry.getKey());
                    targetMap.put(objectFieldName, entry.getValue());
                } else {
                    targetMap.put(entry.getKey(), entry.getValue());
                }
            }
            sourceMap.clear();
            return targetMap;
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * @param index           索引
     * @param page            当前页
     * @param size            每页显示条数
     * @param mustWhere       and查询条件
     * @param shouldWhere     or查询条件
     * @param sortFieldsToAsc 排序字段列表
     * @param includeFields   结果返回字段列表
     * @param excludeFields   结果不返回字段列表
     * @param timeOut
     * @return es查询请求对象
     * @desc 初始化es请求参数
     */
    private SearchRequest initRequestParameters(String index, Integer page, Integer size, Map<String, Object> mustWhere,
                                                Map<String, Object> shouldWhere, Map<String, Boolean> sortFieldsToAsc, String[] includeFields,
                                                String[] excludeFields, Long timeOut, Boolean pagingFlag) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(getQueryBuilder(mustWhere, shouldWhere));
        // 分页
        if (pagingFlag) {
            page = page <= -1 ? 0 : page;
            size = size >= 1000 ? 1000 : size;
            size = size <= 0 ? 15 : size;
            int from = (page - 1) * size;
            sourceBuilder.from(from);
            sourceBuilder.size(size);
        }
        // 超时
        sourceBuilder.timeout(new TimeValue(timeOut, TimeUnit.SECONDS));
        // 排序
        if (sortFieldsToAsc != null && !sortFieldsToAsc.isEmpty()) {
            sortFieldsToAsc.forEach((k, v) -> {
                sourceBuilder.sort(new FieldSortBuilder(k).order(v ? SortOrder.ASC : SortOrder.DESC));
            });
        } else {
            sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        }
        // 返回和排除列
        if (!CollectionUtils.isEmpty(includeFields) || !CollectionUtils.isEmpty(excludeFields)) {
            sourceBuilder.fetchSource(includeFields, excludeFields);
        }
        SearchRequest request = new SearchRequest();
        // 索引
        request.indices(index);
        // 各种组合条件
        request.source(sourceBuilder);
        return request;
    }

    /**
     * @param mustWhere
     * @param shouldWhere
     * @return
     */
    private QueryBuilder getQueryBuilder(Map<String, Object> mustWhere, Map<String, Object> shouldWhere) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // and条件
        if (mustWhere != null && !mustWhere.isEmpty()) {
            mustWhere.forEach((k, v) -> {
                if (k.indexOf("should-") > -1) {
                    BoolQueryBuilder shouldBoolQueryBuilder = QueryBuilders.boolQuery();
                    Map<String, Object> temp = (Map<String, Object>) v;
                    temp.forEach((k1, v1) -> {
                        if (v1 instanceof Map) {
                            // 范围选择map 暂定时间
                            Map<String, Date> mapV = (Map<String, Date>) v1;
                            if (mapV != null) {
                                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k1);
                                if (null != mapV.get("start")) {
                                    rangeQueryBuilder.gte(format.format(mapV.get("start")));
                                }
                                if (null != mapV.get("end")) {
                                    rangeQueryBuilder.lt(format.format(mapV.get("end")));
                                }
                                shouldBoolQueryBuilder.should(rangeQueryBuilder);
//								shouldBoolQueryBuilder.should(QueryBuilders.rangeQuery(k1)
//										.gte(format.format(mapV.get("start"))).lt(format.format(mapV.get("end"))));
                            }
                        } else if (v1 instanceof Object[]) {
                            shouldBoolQueryBuilder.should(QueryBuilders.termsQuery(k1, (Object[]) v1));
                        } else if (FieldUtils.isNumeric(v1 + "")) {
                            shouldBoolQueryBuilder.should(QueryBuilders.matchQuery(k1, v1));
                        } else if (FieldUtils.isEnglishLetter(v1 + "")) {
                            shouldBoolQueryBuilder.should(QueryBuilders.wildcardQuery(k1, "*" + v1.toString() + "*"));
                        } else {
                            // 普通模糊匹配
                            shouldBoolQueryBuilder
                                    .should(QueryBuilders.wildcardQuery(k1 + ".keyword", "*" + v1.toString() + "*"));
                        }
                    });
                    boolQueryBuilder.must(shouldBoolQueryBuilder);
                } else {
                    if (v instanceof Map) {
                        // 范围选择map 暂定时间
                        Map<String, Date> mapV = (Map<String, Date>) v;
                        if (mapV != null) {
                            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k);
                            if (null != mapV.get("start")) {
                                rangeQueryBuilder.gte(format.format(mapV.get("start")));
                            }
                            if (null != mapV.get("end")) {
                                rangeQueryBuilder.lt(format.format(mapV.get("end")));
                            }
                            boolQueryBuilder.must(rangeQueryBuilder);
//							boolQueryBuilder.must(QueryBuilders.rangeQuery(k).gte(format.format(mapV.get("start")))
//									.lt(format.format(mapV.get("end"))));
                        }
                    } else if (v instanceof Object[]) {
                        boolQueryBuilder.must(QueryBuilders.termsQuery(k, (Object[]) v));
                    } else if (FieldUtils.isNumeric(v + "")) {
                        boolQueryBuilder.must(QueryBuilders.matchQuery(k, v));
                    } else if (FieldUtils.isEnglishLetter(v + "")) {
                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(k, "*" + v.toString() + "*"));
                    } else {
                        // 普通模糊匹配
                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(k + ".keyword", "*" + v.toString() + "*"));
                    }
                }
            });
        }
        // 初始化or查询条件
        initShouldWhere(shouldWhere, boolQueryBuilder, format);
        return boolQueryBuilder;
    }

    /**
     * @param shouldWhere
     * @param boolQueryBuilder
     * @param format
     * @desc 初始化or查询条件
     */
    private void initShouldWhere(Map<String, Object> shouldWhere, BoolQueryBuilder boolQueryBuilder,
                                 SimpleDateFormat format) {
        // or条件
        if (shouldWhere != null && !shouldWhere.isEmpty()) {
            shouldWhere.forEach((k, v) -> {
                if (k.indexOf("must-") > -1) {
                    BoolQueryBuilder mustBoolQueryBuilder = QueryBuilders.boolQuery();
                    Map<String, Object> temp = (Map<String, Object>) v;
                    temp.forEach((k1, v1) -> {
                        if (v1 instanceof Map) {
                            // 范围选择map 暂定时间
                            Map<String, Date> mapV = (Map<String, Date>) v1;
                            if (mapV != null) {
                                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k1);
                                if (null != mapV.get("start")) {
                                    rangeQueryBuilder.gte(format.format(mapV.get("start")));
                                }
                                if (null != mapV.get("end")) {
                                    rangeQueryBuilder.lt(format.format(mapV.get("end")));
                                }
                                mustBoolQueryBuilder.must(rangeQueryBuilder);
//								mustBoolQueryBuilder.must(QueryBuilders.rangeQuery(k1)
//										.gte(format.format(mapV.get("start"))).lt(format.format(mapV.get("end"))));
                            }
                        } else if (v1 instanceof Object[]) {
                            mustBoolQueryBuilder.must(QueryBuilders.termsQuery(k1, (Object[]) v1));
                        } else if (FieldUtils.isNumeric(v1 + "")) {
                            mustBoolQueryBuilder.must(QueryBuilders.matchQuery(k1, v1));
                        } else if (FieldUtils.isEnglishLetter(v1 + "")) {
                            mustBoolQueryBuilder.must(QueryBuilders.wildcardQuery(k1, "*" + v1.toString() + "*"));
                        } else {
                            // 普通模糊匹配
                            mustBoolQueryBuilder
                                    .must(QueryBuilders.wildcardQuery(k1 + ".keyword", "*" + v1.toString() + "*"));
                        }
                    });
                    boolQueryBuilder.should(mustBoolQueryBuilder);
                } else {
                    if (v instanceof Map) {
                        // 范围选择map 暂定时间
                        Map<String, Date> mapV = (Map<String, Date>) v;
                        if (mapV != null) {
                            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k);
                            if (null != mapV.get("start")) {
                                rangeQueryBuilder.gte(format.format(mapV.get("start")));
                            }
                            if (null != mapV.get("end")) {
                                rangeQueryBuilder.lt(format.format(mapV.get("end")));
                            }
                            boolQueryBuilder.should(rangeQueryBuilder);
//							boolQueryBuilder.should(QueryBuilders.rangeQuery(k).gte(format.format(mapV.get("start")))
//									.lt(format.format(mapV.get("end"))));
                        }
                    } else if (v instanceof Object[]) {
                        boolQueryBuilder.should(QueryBuilders.termsQuery(k, (Object[]) v));
                    } else if (FieldUtils.isNumeric(v + "")) {
                        boolQueryBuilder.should(QueryBuilders.matchQuery(k, v));
                    } else if (FieldUtils.isEnglishLetter(v + "")) {
                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(k, "*" + v.toString() + "*"));
                    } else {
                        // 普通模糊匹配
                        boolQueryBuilder.should(QueryBuilders.wildcardQuery(k + ".keyword", "*" + v.toString() + "*"));
                    }
                }
            });
        }
    }

    private SearchResponse finByMap(String index, Map<String, String> mustWhere, Map<String, String> matchAll,
                                    BoolQueryBuilder boolBuilder, SearchSourceBuilder sourceBuilder) {
        SearchRequest searchRequest = new SearchRequest(index);

        mustWhere.forEach((key, value) -> {
            if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
                TermQueryBuilder termQuery = QueryBuilders.termQuery(key, value);
                boolBuilder.must(termQuery);
            }
        });

        // 遇到中英文时，需要全词匹配
        if (null != matchAll) {
            matchAll.forEach((key, value) -> {
                if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
                    MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(key, value)
                            .minimumShouldMatch("100%");
                    boolBuilder.must(matchQueryBuilder);
                }
            });
        }

        sourceBuilder.query(boolBuilder);
        searchRequest.source(sourceBuilder);

        SearchResponse response = null;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException elasticsearchStatusException) {
            String msg = elasticsearchStatusException.getDetailedMessage();
            if (msg.contains("index_not_found_exception")) {
                log.error("没有创建索引导致异常，现在创建。indexName:{}", index);
                createIndex(index);
            }
        } catch (IOException e) {
            log.error("查询收藏出错", e);
        }
        return response;
    }

    public Boolean createIndex(String indexName) {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        try {
            client.indices().create(request, RequestOptions.DEFAULT);
            return true;
        } catch (IOException ioException) {
            log.error("创建所以出错", ioException);
        }
        return false;
    }
}
