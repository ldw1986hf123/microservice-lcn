//package com.ldw.metadata.service.impl;
//
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.github.pagehelper.Page;
//import com.ldw.metadata.Util.FieldUtils;
//import com.ldw.metadata.dto.IdDTO;
//import com.ldw.metadata.pageHelper.PageBean;
//import com.ldw.metadata.service.CommonEsService;
//import com.ldw.metadata.vo.CommonSearchVO;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.ArrayUtils;
//import org.apache.commons.lang.StringUtils;
//import org.elasticsearch.ElasticsearchStatusException;
//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.delete.DeleteRequest;
//import org.elasticsearch.action.delete.DeleteResponse;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.index.IndexResponse;
//import org.elasticsearch.action.search.ClearScrollRequest;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.search.SearchScrollRequest;
//import org.elasticsearch.action.update.UpdateRequest;
//import org.elasticsearch.action.update.UpdateResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.indices.CreateIndexRequest;
//import org.elasticsearch.common.unit.TimeValue;
//import org.elasticsearch.common.util.CollectionUtils;
//import org.elasticsearch.common.xcontent.XContentType;
//import org.elasticsearch.index.query.*;
//import org.elasticsearch.index.reindex.BulkByScrollResponse;
//import org.elasticsearch.index.reindex.DeleteByQueryRequest;
//import org.elasticsearch.index.reindex.UpdateByQueryRequest;
//import org.elasticsearch.rest.RestStatus;
//import org.elasticsearch.script.Script;
//import org.elasticsearch.script.ScriptType;
//import org.elasticsearch.search.Scroll;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.aggregations.AggregationBuilder;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.BucketOrder;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
//import org.elasticsearch.search.aggregations.metrics.ParsedSum;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.sort.FieldSortBuilder;
//import org.elasticsearch.search.sort.ScoreSortBuilder;
//import org.elasticsearch.search.sort.SortOrder;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//import static org.elasticsearch.common.unit.TimeValue.timeValueMillis;
//
///**
// * ClassName:CommonEsServiceImpl <br/>
// * Function: 通用的elasticsearch服务接口实现类. <br/>
// * Reason: . <br/>
// * Date: 2020年8月31日 下午3:07:29 <br/>
// *
// * @author WangXf
// * @see
// * @since JDK 1.8
// */
//@Slf4j
//@Service
//@SuppressWarnings("unchecked")
//public class CommonEsServiceImpl implements CommonEsService {
//
//    private RestHighLevelClient client;
//
//    @Override
//    public Boolean add(String index, String id, String jsonEntity, long timeout) {
//        Boolean result = null;
//        try {
//            IndexRequest request = new IndexRequest();
//            request.index(index).id(id).source(jsonEntity, XContentType.JSON)
//                    .timeout(new TimeValue(timeout, TimeUnit.SECONDS));
//            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
//            log.info("added jsonEntity to es successfully !  index:{} , id:{} ",index,id);
//            if (response.status() == RestStatus.OK) {
//                result = true;
//            } else {
//                result = false;
//            }
//        }
//        catch (Exception e){
//            log.error("failed to added jsonEntity to es !", e);
//            result = false;
//        }
//        return result;
//    }
//
//    @Override
//    public Boolean update(String index, String id, String jsonEntity, long timeout) {
//        Boolean result = null;
//        try {
//            UpdateRequest request = new UpdateRequest();
//            request.index(index).id(id).doc(jsonEntity, XContentType.JSON)
//                    .timeout(new TimeValue(timeout, TimeUnit.SECONDS));
//            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
//            log.info("updated jsonEntity to es successfully !");
//            if (response.status() == RestStatus.OK) {
//                result = true;
//            } else {
//                result = false;
//            }
//        } catch (ElasticsearchStatusException elasticsearchStatusException) {
//            String errorMsg = elasticsearchStatusException.getMessage();
//            if (errorMsg.contains("document_missing_exception")) {
//                log.error("index:{},docId:{} ***************************** 不存在", index, id);
//            }
//            result = false;
//        } catch (IOException e) {
//            log.error("failed to updated jsonEntity to es !", e);
//            result = false;
//        }
//        return result;
//    }
//
//    @Override
//    public Boolean updateByQuery(String index, Map<String, Object> mustWhere, Map<String, Object> shouldWhere,
//                                 String updateScript, long timeout) {
//        Boolean result = null;
//        try {
//            UpdateByQueryRequest request = new UpdateByQueryRequest(index);
//            request.setQuery(getQueryBuilder(mustWhere, shouldWhere)).setTimeout(TimeValue.timeValueSeconds(timeout))
//                    .setRefresh(true)
//                    .setScript(new Script(ScriptType.INLINE, "painless", updateScript, Collections.emptyMap()));
//            BulkByScrollResponse response = client.updateByQuery(request, RequestOptions.DEFAULT);
//            log.info("updated jsonEntities to es successfully !");
//            if (0 < response.getStatus().getUpdated()) {
//                result = true;
//            } else {
//                result = false;
//            }
//        } catch (IOException e) {
//            log.error("failed to updated jsonEntities to es !", e);
//            result = false;
//        }
//        return result;
//    }
//
//    @Override
//    public Boolean delete(String index, String id, long timeout) {
//        Boolean result = false;
//        try {
//            DeleteRequest request = new DeleteRequest();
//            request.index(index).id(id).timeout(new TimeValue(timeout, TimeUnit.SECONDS));
//            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
//            log.info("deleted jsonEntity to es successfully !");
//            if (response.status() == RestStatus.OK) {
//                result = true;
//            }
//        } catch (IOException e) {
//            log.error("failed to deleted jsonEntity to es !", e);
//        }
//        return result;
//    }
//
//    @Override
//    public Boolean deleteByQuery(String index, Map<String, Object> mustWhere, Map<String, Object> shouldWhere,
//                                 long timeout) {
//        Boolean result = null;
//        try {
//            DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(index);
//            deleteByQueryRequest.setQuery(getQueryBuilder(mustWhere, shouldWhere))
//                    .setTimeout(TimeValue.timeValueSeconds(timeout)).setRefresh(true);
//            BulkByScrollResponse response = client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
//
//            if (0 < response.getStatus().getUpdated()) {
//                result = true;
//                log.info("删除成功.  index:{},  mustWhere:" ,index, JSONUtil.toJsonStr(mustWhere));
//            } else {
//                result = false;
//                log.info("删除失败.  index:{},  mustWhere:" ,index,  JSONUtil.toJsonStr(mustWhere));
//            }
//        } catch (IOException e) {
//            log.error("failed to deleted jsonEntities to es !", e);
//            result = false;
//        }
//        return result;
//    }
//
//    @Override
//    public Map<String, Object> searchIndex(String index, Integer from, Integer size, Map<String, Object> mustWhere,
//                                           Map<String, Object> shouldWhere, Map<String, Boolean> sortFieldsToAsc, String[] includeFields,
//                                           String[] excludeFields, Long timeOut, Boolean pagingFlag) {
//        Map<String, Object> result = new HashMap<String, Object>();
//        SearchHit[] hits = null;
//        try {
//            // 初始化es查询请求对象
//            SearchRequest request = initRequestParameters(index, from, size, mustWhere, shouldWhere, sortFieldsToAsc,
//                    includeFields, excludeFields, timeOut, pagingFlag);
//            // 请求
//            log.info(request.source().toString());
//            if (pagingFlag) {
//                SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//                // 解析返回
//                if (response.status() != RestStatus.OK || response.getHits().getTotalHits().value <= 0) {
//                    return result;
//                }
//                // 获取source
//                // 将es db字段名转换成对象字段名
//                log.info("响应结果：{}", JSON.toJSONString(response));
//                hits = response.getHits().getHits();
//                long totalElements = response.getHits().getTotalHits().value;
//                long totalPages = 0;
//                if (totalElements % size == 0) {
//                    totalPages = totalElements / size;
//                } else {
//                    totalPages = (totalElements / size) + 1;
//                }
//                result.put("totalElements", totalElements);
//                result.put("totalPages", totalPages);
//            } else {
//                List<SearchHit> searchHits = scrollSearchAll(timeOut, request);
//                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(searchHits)) {
//                    hits = searchHits.toArray(new SearchHit[searchHits.size()]);
////					int sizeHits = searchHits.size();
////					hits = new SearchHit[sizeHits];
////					for (int i = 0; i < sizeHits; i++) {
////						hits[i] = searchHits.get(i);
////					}
//                }
//            }
//            if (CollectionUtils.isEmpty(hits)) {
//                return result;
//            }
//            List<Map<String, Object>> data = dbFieldToObjectField(hits);
//            result.put("content", data);
//            return result;
//        } catch (Exception e) {
//            log.error("分页查询索引异常", e);
//        }
//        return result;
//    }
//
//    /**
//     * @param index           索引
//     * @param page            当前页
//     * @param size            每页显示条数
//     * @param mustWhere       and查询条件
//     * @param shouldWhere     or查询条件
//     * @param sortFieldsToAsc 排序字段列表
//     * @param includeFields   结果返回字段列表
//     * @param excludeFields   结果不返回字段列表
//     * @param timeOut
//     * @return es查询请求对象
//     * @desc 初始化es请求参数
//     */
//    private SearchRequest initRequestParameters(String index, Integer page, Integer size, Map<String, Object> mustWhere,
//                                                Map<String, Object> shouldWhere, Map<String, Boolean> sortFieldsToAsc, String[] includeFields,
//                                                String[] excludeFields, Long timeOut, Boolean pagingFlag) {
////		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
////		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
////		// and条件
////		if (mustWhere != null && !mustWhere.isEmpty()) {
////			mustWhere.forEach((k, v) -> {
////				if (k.indexOf("should-") > -1) {
////					BoolQueryBuilder shouldBoolQueryBuilder = QueryBuilders.boolQuery();
////					Map<String, Object> temp = (Map<String, Object>) v;
////					temp.forEach((k1, v1) -> {
////						if (v1 instanceof Map) {
////							// 范围选择map 暂定时间
////							Map<String, Date> mapV = (Map<String, Date>) v1;
////							if (mapV != null) {
////								RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k1);
////								if (null != mapV.get("start")) {
////									rangeQueryBuilder.gte(format.format(mapV.get("start")));
////								}
////								if (null != mapV.get("end")) {
////									rangeQueryBuilder.lt(format.format(mapV.get("end")));
////								}
////								shouldBoolQueryBuilder.should(rangeQueryBuilder);
//////								shouldBoolQueryBuilder.should(QueryBuilders.rangeQuery(k1)
//////										.gte(format.format(mapV.get("start"))).lt(format.format(mapV.get("end"))));
////							}
////						} else if (v1 instanceof Object[]) {
////							shouldBoolQueryBuilder.should(QueryBuilders.termsQuery(k1, (Object[]) v1));
////						} else if (FieldUtils.isNumeric(v1 + "")) {
////							shouldBoolQueryBuilder.should(QueryBuilders.matchQuery(k1, v1));
////						} else if (FieldUtils.isEnglishLetter(v1 + "")) {
////							shouldBoolQueryBuilder.should(QueryBuilders.wildcardQuery(k1, "*" + v1.toString() + "*"));
////						} else {
////							// 普通模糊匹配
////							shouldBoolQueryBuilder
////									.should(QueryBuilders.wildcardQuery(k1 + ".keyword", "*" + v1.toString() + "*"));
////						}
////					});
////					boolQueryBuilder.must(shouldBoolQueryBuilder);
////				} else {
////					if (v instanceof Map) {
////						// 范围选择map 暂定时间
////						Map<String, Date> mapV = (Map<String, Date>) v;
////						if (mapV != null) {
////							RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k);
////							if (null != mapV.get("start")) {
////								rangeQueryBuilder.gte(format.format(mapV.get("start")));
////							}
////							if (null != mapV.get("end")) {
////								rangeQueryBuilder.lt(format.format(mapV.get("end")));
////							}
////							boolQueryBuilder.must(rangeQueryBuilder);
//////							boolQueryBuilder.must(QueryBuilders.rangeQuery(k).gte(format.format(mapV.get("start")))
//////									.lt(format.format(mapV.get("end"))));
////						}
////					} else if (v instanceof Object[]) {
////						boolQueryBuilder.must(QueryBuilders.termsQuery(k, (Object[]) v));
////					} else if (FieldUtils.isNumeric(v + "")) {
////						boolQueryBuilder.must(QueryBuilders.matchQuery(k, v));
////					} else if (FieldUtils.isEnglishLetter(v + "")) {
////						boolQueryBuilder.must(QueryBuilders.wildcardQuery(k, "*" + v.toString() + "*"));
////					} else {
////						// 普通模糊匹配
////						boolQueryBuilder.must(QueryBuilders.wildcardQuery(k + ".keyword", "*" + v.toString() + "*"));
////					}
////				}
////			});
////			sourceBuilder.query(boolQueryBuilder);
////		}
////		// 初始化or查询条件
////		initShouldWhere(shouldWhere, sourceBuilder, boolQueryBuilder, format);
//        sourceBuilder.query(getQueryBuilder(mustWhere, shouldWhere));
//        // 分页
//        if (pagingFlag) {
//            page = page <= -1 ? 0 : page;
//            size = size >= 1000 ? 1000 : size;
//            size = size <= 0 ? 15 : size;
//            int from = (page - 1) * size;
//            sourceBuilder.from(from);
//            sourceBuilder.size(size);
//        }
//        // 超时
//        sourceBuilder.timeout(new TimeValue(timeOut, TimeUnit.SECONDS));
//        // 排序
//        if (sortFieldsToAsc != null && !sortFieldsToAsc.isEmpty()) {
//            sortFieldsToAsc.forEach((k, v) -> {
//                sourceBuilder.sort(new FieldSortBuilder(k).order(v ? SortOrder.ASC : SortOrder.DESC));
//            });
//        } else {
//            sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
//        }
//        // 返回和排除列
//        if (!CollectionUtils.isEmpty(includeFields) || !CollectionUtils.isEmpty(excludeFields)) {
//            sourceBuilder.fetchSource(includeFields, excludeFields);
//        }
//        SearchRequest request = new SearchRequest();
//        // 索引
//        request.indices(index);
//        // 各种组合条件
//        request.source(sourceBuilder);
//        return request;
//    }
//
////	/**
////	 * @desc 初始化or查询条件
////	 *
////	 * @param shouldWhere
////	 * @param sourceBuilder
////	 * @param boolQueryBuilder
////	 */
////	private void initShouldWhere(Map<String, Object> shouldWhere, SearchSourceBuilder sourceBuilder,
////			BoolQueryBuilder boolQueryBuilder, SimpleDateFormat format) {
////		// or条件
////		if (shouldWhere != null && !shouldWhere.isEmpty()) {
////			shouldWhere.forEach((k, v) -> {
////				if (k.indexOf("must-") > -1) {
////					BoolQueryBuilder mustBoolQueryBuilder = QueryBuilders.boolQuery();
////					Map<String, Object> temp = (Map<String, Object>) v;
////					temp.forEach((k1, v1) -> {
////						if (v1 instanceof Map) {
////							// 范围选择map 暂定时间
////							Map<String, Date> mapV = (Map<String, Date>) v1;
////							if (mapV != null) {
////								RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k1);
////								if (null != mapV.get("start")) {
////									rangeQueryBuilder.gte(format.format(mapV.get("start")));
////								}
////								if (null != mapV.get("end")) {
////									rangeQueryBuilder.lt(format.format(mapV.get("end")));
////								}
////								mustBoolQueryBuilder.must(rangeQueryBuilder);
//////								mustBoolQueryBuilder.must(QueryBuilders.rangeQuery(k1)
//////										.gte(format.format(mapV.get("start"))).lt(format.format(mapV.get("end"))));
////							}
////						} else if (v1 instanceof Object[]) {
////							mustBoolQueryBuilder.must(QueryBuilders.termsQuery(k1, (Object[]) v1));
////						} else if (FieldUtils.isNumeric(v1 + "")) {
////							mustBoolQueryBuilder.must(QueryBuilders.matchQuery(k1, v1));
////						} else if (FieldUtils.isEnglishLetter(v1 + "")) {
////							mustBoolQueryBuilder.must(QueryBuilders.wildcardQuery(k1, "*" + v1.toString() + "*"));
////						} else {
////							// 普通模糊匹配
////							mustBoolQueryBuilder
////									.must(QueryBuilders.wildcardQuery(k1 + ".keyword", "*" + v1.toString() + "*"));
////						}
////					});
////					boolQueryBuilder.should(mustBoolQueryBuilder);
////				} else {
////					if (v instanceof Map) {
////						// 范围选择map 暂定时间
////						Map<String, Date> mapV = (Map<String, Date>) v;
////						if (mapV != null) {
////							RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k);
////							if (null != mapV.get("start")) {
////								rangeQueryBuilder.gte(format.format(mapV.get("start")));
////							}
////							if (null != mapV.get("end")) {
////								rangeQueryBuilder.lt(format.format(mapV.get("end")));
////							}
////							boolQueryBuilder.should(rangeQueryBuilder);
//////							boolQueryBuilder.should(QueryBuilders.rangeQuery(k).gte(format.format(mapV.get("start")))
//////									.lt(format.format(mapV.get("end"))));
////						}
////					} else if (v instanceof Object[]) {
////						boolQueryBuilder.should(QueryBuilders.termsQuery(k, (Object[]) v));
////					} else if (FieldUtils.isNumeric(v + "")) {
////						boolQueryBuilder.should(QueryBuilders.matchQuery(k, v));
////					} else if (FieldUtils.isEnglishLetter(v + "")) {
////						boolQueryBuilder.must(QueryBuilders.wildcardQuery(k, "*" + v.toString() + "*"));
////					} else {
////						// 普通模糊匹配
////						boolQueryBuilder.should(QueryBuilders.wildcardQuery(k + ".keyword", "*" + v.toString() + "*"));
////					}
////				}
////			});
////			sourceBuilder.query(boolQueryBuilder);
////		}
////	}
//
//    /**
//     * @return
//     * @desc 将es db字段名转换成对象字段名
//     */
//    private List<Map<String, Object>> dbFieldToObjectField(SearchHit... hits) {
//        List<Map<String, Object>> result = Arrays.stream(hits).map(b -> {
//            Map<String, Object> sourceMap = b.getSourceAsMap();
//            Map<String, Object> targetMap = new HashMap<String, Object>();
//            Iterator<Map.Entry<String, Object>> it = sourceMap.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry<String, Object> entry = it.next();
//                if (entry.getKey().indexOf("_") > -1) {
//                    String objectFieldName = FieldUtils.dbFieldNameToObjectName(entry.getKey());
//                    targetMap.put(objectFieldName, entry.getValue());
//                } else {
//                    targetMap.put(entry.getKey(), entry.getValue());
//                }
//            }
//            sourceMap.clear();
//            return targetMap;
//        }).collect(Collectors.toList());
//        return result;
//    }
//
//    /**
//     * @param mustWhere
//     * @param shouldWhere
//     * @return
//     */
//    private QueryBuilder getQueryBuilder(Map<String, Object> mustWhere, Map<String, Object> shouldWhere) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        // and条件
//        if (mustWhere != null && !mustWhere.isEmpty()) {
//            mustWhere.forEach((k, v) -> {
//                if (k.indexOf("should-") > -1) {
//                    BoolQueryBuilder shouldBoolQueryBuilder = QueryBuilders.boolQuery();
//                    Map<String, Object> temp = (Map<String, Object>) v;
//                    temp.forEach((k1, v1) -> {
//                        if (v1 instanceof Map) {
//                            // 范围选择map 暂定时间
//                            Map<String, Date> mapV = (Map<String, Date>) v1;
//                            if (mapV != null) {
//                                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k1);
//                                if (null != mapV.get("start")) {
//                                    rangeQueryBuilder.gte(format.format(mapV.get("start")));
//                                }
//                                if (null != mapV.get("end")) {
//                                    rangeQueryBuilder.lt(format.format(mapV.get("end")));
//                                }
//                                shouldBoolQueryBuilder.should(rangeQueryBuilder);
////								shouldBoolQueryBuilder.should(QueryBuilders.rangeQuery(k1)
////										.gte(format.format(mapV.get("start"))).lt(format.format(mapV.get("end"))));
//                            }
//                        } else if (v1 instanceof Object[]) {
//                            shouldBoolQueryBuilder.should(QueryBuilders.termsQuery(k1, (Object[]) v1));
//                        } else if (FieldUtils.isNumeric(v1 + "")) {
//                            shouldBoolQueryBuilder.should(QueryBuilders.matchQuery(k1, v1));
//                        } else if (FieldUtils.isEnglishLetter(v1 + "")) {
//                            shouldBoolQueryBuilder.should(QueryBuilders.wildcardQuery(k1, "*" + v1.toString() + "*"));
//                        } else {
//                            // 普通模糊匹配
//                            shouldBoolQueryBuilder
//                                    .should(QueryBuilders.wildcardQuery(k1 + ".keyword", "*" + v1.toString() + "*"));
//                        }
//                    });
//                    boolQueryBuilder.must(shouldBoolQueryBuilder);
//                } else {
//                    if (v instanceof Map) {
//                        // 范围选择map 暂定时间
//                        Map<String, Date> mapV = (Map<String, Date>) v;
//                        if (mapV != null) {
//                            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k);
//                            if (null != mapV.get("start")) {
//                                rangeQueryBuilder.gte(format.format(mapV.get("start")));
//                            }
//                            if (null != mapV.get("end")) {
//                                rangeQueryBuilder.lt(format.format(mapV.get("end")));
//                            }
//                            boolQueryBuilder.must(rangeQueryBuilder);
////							boolQueryBuilder.must(QueryBuilders.rangeQuery(k).gte(format.format(mapV.get("start")))
////									.lt(format.format(mapV.get("end"))));
//                        }
//                    } else if (v instanceof Object[]) {
//                        boolQueryBuilder.must(QueryBuilders.termsQuery(k, (Object[]) v));
//                    } else if (FieldUtils.isNumeric(v + "")) {
//                        boolQueryBuilder.must(QueryBuilders.matchQuery(k, v));
//                    } else if (FieldUtils.isEnglishLetter(v + "")) {
//                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(k, "*" + v.toString() + "*"));
//                    } else {
//                        // 普通模糊匹配
//                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(k + ".keyword", "*" + v.toString() + "*"));
//                    }
//                }
//            });
//        }
//        // 初始化or查询条件
//        initShouldWhere(shouldWhere, boolQueryBuilder, format);
//        return boolQueryBuilder;
//    }
//
//    /**
//     * @param shouldWhere
//     * @param boolQueryBuilder
//     * @param format
//     * @desc 初始化or查询条件
//     */
//    private void initShouldWhere(Map<String, Object> shouldWhere, BoolQueryBuilder boolQueryBuilder,
//                                 SimpleDateFormat format) {
//        // or条件
//        if (shouldWhere != null && !shouldWhere.isEmpty()) {
//            shouldWhere.forEach((k, v) -> {
//                if (k.indexOf("must-") > -1) {
//                    BoolQueryBuilder mustBoolQueryBuilder = QueryBuilders.boolQuery();
//                    Map<String, Object> temp = (Map<String, Object>) v;
//                    temp.forEach((k1, v1) -> {
//                        if (v1 instanceof Map) {
//                            // 范围选择map 暂定时间
//                            Map<String, Date> mapV = (Map<String, Date>) v1;
//                            if (mapV != null) {
//                                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k1);
//                                if (null != mapV.get("start")) {
//                                    rangeQueryBuilder.gte(format.format(mapV.get("start")));
//                                }
//                                if (null != mapV.get("end")) {
//                                    rangeQueryBuilder.lt(format.format(mapV.get("end")));
//                                }
//                                mustBoolQueryBuilder.must(rangeQueryBuilder);
////								mustBoolQueryBuilder.must(QueryBuilders.rangeQuery(k1)
////										.gte(format.format(mapV.get("start"))).lt(format.format(mapV.get("end"))));
//                            }
//                        } else if (v1 instanceof Object[]) {
//                            mustBoolQueryBuilder.must(QueryBuilders.termsQuery(k1, (Object[]) v1));
//                        } else if (FieldUtils.isNumeric(v1 + "")) {
//                            mustBoolQueryBuilder.must(QueryBuilders.matchQuery(k1, v1));
//                        } else if (FieldUtils.isEnglishLetter(v1 + "")) {
//                            mustBoolQueryBuilder.must(QueryBuilders.wildcardQuery(k1, "*" + v1.toString() + "*"));
//                        } else {
//                            // 普通模糊匹配
//                            mustBoolQueryBuilder
//                                    .must(QueryBuilders.wildcardQuery(k1 + ".keyword", "*" + v1.toString() + "*"));
//                        }
//                    });
//                    boolQueryBuilder.should(mustBoolQueryBuilder);
//                } else {
//                    if (v instanceof Map) {
//                        // 范围选择map 暂定时间
//                        Map<String, Date> mapV = (Map<String, Date>) v;
//                        if (mapV != null) {
//                            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k);
//                            if (null != mapV.get("start")) {
//                                rangeQueryBuilder.gte(format.format(mapV.get("start")));
//                            }
//                            if (null != mapV.get("end")) {
//                                rangeQueryBuilder.lt(format.format(mapV.get("end")));
//                            }
//                            boolQueryBuilder.should(rangeQueryBuilder);
////							boolQueryBuilder.should(QueryBuilders.rangeQuery(k).gte(format.format(mapV.get("start")))
////									.lt(format.format(mapV.get("end"))));
//                        }
//                    } else if (v instanceof Object[]) {
//                        boolQueryBuilder.should(QueryBuilders.termsQuery(k, (Object[]) v));
//                    } else if (FieldUtils.isNumeric(v + "")) {
//                        boolQueryBuilder.should(QueryBuilders.matchQuery(k, v));
//                    } else if (FieldUtils.isEnglishLetter(v + "")) {
//                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(k, "*" + v.toString() + "*"));
//                    } else {
//                        // 普通模糊匹配
//                        boolQueryBuilder.should(QueryBuilders.wildcardQuery(k + ".keyword", "*" + v.toString() + "*"));
//                    }
//                }
//            });
//        }
//    }
//
//    /**
//     * 根据 id 和索引 更新指定索引中的文档
//     *
//     * @return
//     * @throws IOException
//     */
//    @Override
//    public UpdateResponse updateDoc(String index, String docId, Map map) throws IOException {
//        UpdateRequest request = new UpdateRequest(index, docId);
//        request.doc(map);
//        return client.update(request, RequestOptions.DEFAULT);
//    }
//
//    /**
//     * 直接获取某个index下的满足条件的doc， 返回一个所哟有doc的json字符串集合list
//     *
//     * @param index
//     * @return
//     */
//    @Override
//    public List<JSONObject> findByMap(String index, Map<String, String> mustWhere) {
//
//        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
//        mustWhere.forEach((key, value) -> {
//            if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
//                TermQueryBuilder termQuery = QueryBuilders.termQuery(key, value);
//                boolBuilder.must(termQuery);
//            }
//        });
//
//        List<JSONObject> jsonObjectList = null;
//        try {
//            jsonObjectList = scrollSearchAll(index, boolBuilder);
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
//        return jsonObjectList;
//    }
//
//    /**
//     * 查询某个范围内的记录
//     *
//     * @param index
//     * @param mustWhere
//     * @param rangeField 要筛选的最小值
//     * @param max        筛选的最大只
//     * @param min        筛选的结束时间
//     * @return 指挥返回10条记录
//     */
//    @Override
//    public List<JSONObject> findByMapAndRange(String index, Map<String, String> mustWhere, String rangeField,
//                                              Object min, Object max) {
//        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
//        mustWhere.forEach((key, value) -> {
//            if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
//                TermQueryBuilder termQuery = QueryBuilders.termQuery(key, value);
//                boolBuilder.must(termQuery);
//            }
//        });
//
//        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(rangeField);
//        rangeQueryBuilder.gte(min);
//        rangeQueryBuilder.lte(max);
//        boolBuilder.must(rangeQueryBuilder);
//
//        List<JSONObject> jsonObjectList = null;
//        try {
//            jsonObjectList = scrollSearchAll(index, boolBuilder);
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
//        return jsonObjectList;
//    }
//
//    private SearchResponse finByMap(String index, Map<String, String> mustWhere, Map<String, String> matchAll,
//                                    BoolQueryBuilder boolBuilder, SearchSourceBuilder sourceBuilder) {
//        SearchRequest searchRequest = new SearchRequest(index);
//
//        mustWhere.forEach((key, value) -> {
//            if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
//                TermQueryBuilder termQuery = QueryBuilders.termQuery(key, value);
//                boolBuilder.must(termQuery);
//            }
//        });
//
//        // 遇到中英文时，需要全词匹配
//        if (null != matchAll) {
//            matchAll.forEach((key, value) -> {
//                if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
//                    MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(key, value)
//                            .minimumShouldMatch("100%");
//                    boolBuilder.must(matchQueryBuilder);
//                }
//            });
//        }
//
//        sourceBuilder.query(boolBuilder);
//        searchRequest.source(sourceBuilder);
//
//        SearchResponse response = null;
//        try {
//            response = client.search(searchRequest, RequestOptions.DEFAULT);
//        } catch (ElasticsearchStatusException elasticsearchStatusException) {
//            String msg = elasticsearchStatusException.getDetailedMessage();
//            if (msg.contains("index_not_found_exception")) {
//                log.error("没有创建索引导致异常，现在创建。indexName:{}", index);
//                createIndex(index);
//            }
//        } catch (IOException e) {
//            log.error("查询收藏出错", e);
//        }
//        return response;
//    }
//
//    /**
//     * 直接获取某个index下的满足条件的doc， 分页返回一个所哟有doc的json字符串集合list
//     *
//     * @return
//     * @throws IOException
//     */
//    @Override
//    public Map<String, Object> findByMap(String index, Map<String, String> mustWhere, Integer page, Integer size) {
//        Map<String, Object> resultMap = new HashMap();
//        List<String> resultList = new ArrayList<>(10);
//
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        if (null != page && null != size) {
//            sourceBuilder.from((page - 1) * size);
//            sourceBuilder.size(size);
//        }
//        long totalElements = 0L;
//        SearchResponse response = finByMap(index, mustWhere, null, QueryBuilders.boolQuery(), sourceBuilder);
//        if (null != response) {
//            SearchHits hits = response.getHits();
//            totalElements = response.getHits().getTotalHits().value;
//            for (SearchHit hit : hits) {
//                resultList.add(hit.getSourceAsString());
//            }
//        }
//
//        resultMap.put("resultList", resultList);
//        resultMap.put("totalElements", totalElements);
//        return resultMap;
//    }
//
//    /**
//     * 直接获取某个index下的满足条件的doc， 分页返回一个所哟有doc的json字符串集合list
//     *
//     * @return
//     * @throws IOException
//     */
//    @Override
//    public <T> PageBean<T> findByMap(String index, Map<String, String> mustWhere, Integer page, Integer size, Class<T> clazz) {
//        PageBean<T> resultPageBean = new PageBean<>();
//        List<T> resultList = new ArrayList<>(10);
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        if (null != page && null != size) {
//            sourceBuilder.from((page - 1) * size);
//            sourceBuilder.size(size);
//        }
//        long totalElements = 0L;
//
//        try {
//            SearchResponse response = finByMap(index, mustWhere, null, QueryBuilders.boolQuery(), sourceBuilder);
//            if (null != response) {
//                SearchHits hits = response.getHits();
//                totalElements = response.getHits().getTotalHits().value;
//                for (SearchHit hit : hits) {
//                    T bean = clazz.newInstance();
//                    String jsonStr = hit.getSourceAsString();
//                    cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
//                    bean = JSONUtil.toBean(jsonObject, clazz, true);
//                    resultList.add(bean);
//                }
//            }
//        } catch (IllegalAccessException e) {
//            log.error("findByMap 异常", e);
//        } catch (InstantiationException e) {
//            log.error("findByMap 异常", e);
//        }
//
//        Page<T> resultPage = new Page<>();
//        resultPage.setPageNum(page);
//        resultPage.setPageSize(size);
//        resultPage.setTotal(totalElements);
//        // 设置返回数据信息
//        resultPage.addAll(resultList);
//        resultPageBean = new PageBean<>(resultPage);
//        return resultPageBean;
//    }
//
//
//    /**
//     * 直接获取某个index下的满足条件的doc， 分页返回一个所哟有doc的json字符串集合list
//     *
//     * @return
//     * @throws IOException
//     */
//    @Override
//    public Map<String, Object> findByMap(String index, Map<String, String> mustWhere, Integer page, Integer size,
//                                         String sortedFiled, String sorted) {
//        Map<String, Object> resultMap = new HashMap(2);
//        List<String> resultList = new ArrayList<>(10);
//
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        if (null != page && null != size) {
//            sourceBuilder.from((page - 1) * size);
//            sourceBuilder.size(size);
//        }
//
//        // 按照id字段情况升序
//        if ("ASC".equals(sorted)) {
//            sourceBuilder.sort(new FieldSortBuilder(sortedFiled).order(SortOrder.ASC));
//        } else {
//            sourceBuilder.sort(new FieldSortBuilder(sortedFiled).order(SortOrder.DESC));
//        }
//        SearchResponse response = finByMap(index, mustWhere, null, QueryBuilders.boolQuery(), sourceBuilder);
//
//        if (null != response) {
//            SearchHits hits = response.getHits();
//            long totalElements = response.getHits().getTotalHits().value;
//            for (SearchHit hit : hits) {
//                resultList.add(hit.getSourceAsString());
//            }
//            resultMap.put("resultList", resultList);
//            resultMap.put("totalElements", totalElements);
//        }
//
//        return resultMap;
//    }
//
//    /**
//     * 需要中英文全词匹配的时候调用改方法
//     *
//     * @return
//     * @throws IOException
//     */
//    @Override
//    public Map<String, Object> findByMap(String index, Map<String, String> mustWhere, Map<String, String> matchAll,
//                                         Integer page, Integer size, String sortedFiled, String sorted) {
//        Map<String, Object> resultMap = new HashMap(2);
//        List<String> resultList = new ArrayList<>(10);
//
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        if (null != page && null != size) {
//            sourceBuilder.from((page - 1) * size);
//            sourceBuilder.size(size);
//        }
//
//        // 按照id字段情况升序
//        if ("ASC".equals(sorted)) {
//            sourceBuilder.sort(new FieldSortBuilder(sortedFiled).order(SortOrder.ASC));
//        } else {
//            sourceBuilder.sort(new FieldSortBuilder(sortedFiled).order(SortOrder.DESC));
//        }
//        SearchResponse response = finByMap(index, mustWhere, matchAll, QueryBuilders.boolQuery(), sourceBuilder);
//
//        if (null != response) {
//            SearchHits hits = response.getHits();
//            long totalElements = response.getHits().getTotalHits().value;
//            for (SearchHit hit : hits) {
//                resultList.add(hit.getSourceAsString());
//            }
//            resultMap.put("resultList", resultList);
//            resultMap.put("totalElements", totalElements);
//        }
//        return resultMap;
//    }
//
//    @Override
//    public Boolean createIndex(String indexName) {
//        CreateIndexRequest request = new CreateIndexRequest(indexName);
//        try {
//            client.indices().create(request, RequestOptions.DEFAULT);
//            return true;
//        } catch (IOException ioException) {
//            log.error("创建所以出错", ioException);
//        }
//        return false;
//    }
//
//    @Override
//    public <T extends IdDTO> Boolean batchAdd(String index, List<T> sources, Integer timeOut) {
//        BulkRequest bulkRequest = new BulkRequest();
//        for (T source : sources) {
//            IndexRequest request = new IndexRequest(index);
//            String json = JSONObject.toJSONString(source);
//            String id = String.valueOf(source.getId());
//            request.id(id).source(json, XContentType.JSON).timeout(new TimeValue(timeOut, TimeUnit.SECONDS));
//            bulkRequest.add(request);
//        }
//
//        try {
//            client.bulk(bulkRequest, RequestOptions.DEFAULT);
//            return true;
//        } catch (IOException e) {
//            log.info("批量保存ES失败！");
//           e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * 使用游标获取全部结果，返回SearchHit集合
//     *
//     * @param index
//     * @return
//     * @throws IOException
//     */
//    private List<JSONObject> scrollSearchAll(String index, BoolQueryBuilder boolBuilder) throws IOException {
//        List<JSONObject> resultList = new ArrayList<>();
//
//        SearchRequest searchRequest = new SearchRequest(index);
//
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        sourceBuilder.query(boolBuilder);
//
//        Scroll scroll = new Scroll(timeValueMillis(30));
//        searchRequest.source(sourceBuilder);
//        searchRequest.scroll(scroll);
//
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        String scrollId = searchResponse.getScrollId();
//        SearchHit[] hits = searchResponse.getHits().getHits();
//        while (ArrayUtils.isNotEmpty(hits)) {
//            for (SearchHit hit : hits) {
//                String jsonStr = hit.getSourceAsString();
//                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
//                resultList.add(jsonObject);
//            }
//            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
//            searchScrollRequest.scroll(scroll);
//            SearchResponse searchScrollResponse = client.scroll(searchScrollRequest, RequestOptions.DEFAULT);
//            scrollId = searchScrollResponse.getScrollId();
//            hits = searchScrollResponse.getHits().getHits();
//        }
//        // 及时清除es快照，释放资源
//        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
//        clearScrollRequest.addScrollId(scrollId);
//        client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
//        return resultList;
//    }
//
//    @Override
//    public List<Map<String, Long>> sumGroupByField(String index, Map<String, String> mustWhere, String groupField,
//                                                   String sumField) {
//        List resultList = new ArrayList();
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//
//        String aliasGroup = "group_" + groupField;
//        String aliasSumName = "sum_" + sumField;
//
//        TermsAggregationBuilder aggregationBuilder1 = AggregationBuilders.terms(aliasGroup).field(groupField)
//                .order(BucketOrder.aggregation(aliasSumName, false));
//        AggregationBuilder aggregationBuilder = AggregationBuilders.sum(aliasSumName).field(sumField);
//
//        sourceBuilder.aggregation(aggregationBuilder1.subAggregation(aggregationBuilder));
//
//        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
//        mustWhere.forEach((key, value) -> {
//            if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
//                TermQueryBuilder termQuery = QueryBuilders.termQuery(key, value);
//                boolBuilder.must(termQuery);
//            }
//        });
//
//        // 绑定查询条件
//        SearchRequest searchRequest = new SearchRequest(index);
//        searchRequest.source(sourceBuilder);
//        sourceBuilder.query(boolBuilder);
//
//        SearchResponse responses = null;
//        try {
//            responses = client.search(searchRequest, RequestOptions.DEFAULT);
//            Terms aggregation = responses.getAggregations().get(aliasGroup);
//            for (Terms.Bucket bucket : aggregation.getBuckets()) {
//
//                ParsedSum sumValue = bucket.getAggregations().get(aliasSumName);
//                Map<String, Object> singleMap = new HashMap<>(10);
//
//                if (null != sumField) {
//                    Double doubleSumValue = Double.valueOf(sumValue.getValue());
//                    log.info(bucket.getKey() + ": " + doubleSumValue);
//                    singleMap.put(String.valueOf(bucket.getKey()), doubleSumValue.longValue());
//                    resultList.add(singleMap);
//                }
//            }
//        } catch (ElasticsearchStatusException elasticsearchStatusException) {
//            String msg = elasticsearchStatusException.getDetailedMessage();
//            if (msg.contains("index_not_found_exception")) {
//                log.error("没有创建索引导致异常，现在创建。indexName:{}", index);
//                createIndex(index);
//            }
//        } catch (IOException ioException) {
//            log.error("排序出错", ioException);
//        }
//        return resultList;
//    }
//
//    /**
//     * @param scrollTimeOut
//     * @param searchRequest
//     * @return
//     * @throws IOException
//     * @desc 使用游标获取全部结果，返回SearchHit集合
//     */
//    @SuppressWarnings("deprecation")
//    private List<SearchHit> scrollSearchAll(Long scrollTimeOut, SearchRequest searchRequest) throws IOException {
//        Scroll scroll = new Scroll(new TimeValue(scrollTimeOut, TimeUnit.SECONDS));
//        searchRequest.scroll(scroll);
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        String scrollId = searchResponse.getScrollId();
//        SearchHit[] hits = searchResponse.getHits().getHits();
//        List<SearchHit> resultSearchHit = new ArrayList<>();
//        while (ArrayUtils.isNotEmpty(hits)) {
//            for (SearchHit hit : hits) {
//                resultSearchHit.add(hit);
//            }
//            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
//            searchScrollRequest.scroll(scroll);
//            SearchResponse searchScrollResponse = client.searchScroll(searchScrollRequest, RequestOptions.DEFAULT);
//            scrollId = searchScrollResponse.getScrollId();
//            hits = searchScrollResponse.getHits().getHits();
//        }
//        // 及时清除es快照，释放资源
//        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
//        clearScrollRequest.addScrollId(scrollId);
//        client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
//        return resultSearchHit;
//    }
//
//    @Override
//    public <T> T findOne(CommonSearchVO<T> data) {
//        try {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//            Map<String, Object> mustWhere = data.getMustWhere();
//            if (mustWhere != null && !mustWhere.isEmpty()) {
//                mustWhere.forEach((k, v) -> {
//                    if (v instanceof Map) {
//                        Map<String, Date> mapV = (Map<String, Date>) v;
//                        if (mapV != null) {
//                            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(k);
//                            if (null != mapV.get("start")) {
//                                rangeQueryBuilder.gte(format.format(mapV.get("start")));
//                            }
//                            if (null != mapV.get("end")) {
//                                rangeQueryBuilder.lt(format.format(mapV.get("end")));
//                            }
//                            boolQueryBuilder.must(rangeQueryBuilder);
//                        }
//                    } else if (v instanceof Object[]) {
//                        boolQueryBuilder.must(QueryBuilders.termsQuery(k, (Object[]) v));
//                    } else if (FieldUtils.isNumeric(v + "")) {
//                        boolQueryBuilder.must(QueryBuilders.matchQuery(k, v));
//                    } else if (FieldUtils.isEnglishLetter(v + "")) {
//                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(k, v.toString()));
//                    } else {
//                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(k + ".keyword", v.toString()));
//                    }
//                });
//            }
//            sourceBuilder.query(boolQueryBuilder);
//            sourceBuilder.timeout(new TimeValue(data.getTimeout(), TimeUnit.SECONDS));
//            if (!CollectionUtils.isEmpty(data.getIncludeFields())
//                    || !CollectionUtils.isEmpty(data.getExcludeFields())) {
//                sourceBuilder.fetchSource(data.getIncludeFields(), data.getExcludeFields());
//            }
//            SearchRequest request = new SearchRequest();
//            request.indices(data.getIndex());
//            request.source(sourceBuilder);
//            log.info(request.source().toString());
//            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//            if (response.status() != RestStatus.OK || response.getHits().getTotalHits().value <= 0) {
//                return data.getClassObject().newInstance();
//            }
//            log.info("响应结果：{}", JSON.toJSONString(response));
//            List<Map<String, Object>> result = dbFieldToObjectField(response.getHits().getHits());
//            return JSON.parseObject(JSON.toJSONString(result.get(0)), data.getClassObject());
//        } catch (Exception e) {
//            log.error("failed to found single entity !", e);
//        }
//        T responseData = null;
//        try {
//            responseData = data.getClassObject().newInstance();
//        } catch (Exception e) {
//            log.error("failed to found single entity !", e);
//        }
//        return responseData;
//    }
//
//
//    @Override
//    public <T> T findById(String index, String id, Class<T> clazz) {
//        T resultBean = null;
//        try {
//            resultBean = clazz.newInstance();
//            Map mustWhere = new HashMap();
//            mustWhere.put("id", id);
//            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//            SearchResponse response = finByMap(index, mustWhere, null, QueryBuilders.boolQuery(), sourceBuilder);
//            if (null != response) {
//                SearchHits hits = response.getHits();
//                for (SearchHit hit : hits) {
//                    String jsonStr = hit.getSourceAsString();
//                    resultBean = JSONUtil.toBean(jsonStr, clazz);
//                }
//            }
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        return resultBean;
//
//    }
//
//
//}
