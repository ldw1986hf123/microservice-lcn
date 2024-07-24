package com.ldw.microservice.service;

import com.ldw.microservice.query.CommonSearchQuery;

import java.util.List;
import java.util.Map;

/**
 *
 * @Author 卢丹文
 * @Description
 */
public interface CommonEsBizService {

    <T> T findOne(CommonSearchQuery<T> data);

    <T> PageBean<T> findByMap(String index, Map<String, String> mustWhere, Integer page, Integer size, Class<T> clazz, String sortedFiled, String sorted);

    /**
     * @param index           索引
     * @param from            当前页
     * @param size            每页显示条数
     * @param mustWhere       and查询条件
     * @param shouldWhere     or查询条件
     * @param sortFieldsToAsc 排序字段列表
     * @param includeFields   结果返回字段列表
     * @param excludeFields   结果不返回字段列表
     * @param timeOut
     * @return 结果集合列表
     * @desc 分页查询索引
     */
    public <T> PageBean<T> pageIndex(String index, Integer from, Integer size, Map<String, Object> mustWhere,
                                     Map<String, Object> shouldWhere, Map<String, Boolean> sortFieldsToAsc, String[] includeFields,
                                     String[] excludeFields, Long timeOut, Class<T> tClass);
    /**
     * @param index           索引
     * @param mustWhere       and查询条件
     * @param shouldWhere     or查询条件
     * @param sortFieldsToAsc 排序字段列表
     * @param includeFields   结果返回字段列表
     * @param excludeFields   结果不返回字段列表
     * @param timeOut
     * @param tClass
     * @return 结果集合列表
     * @desc 查询索引
     */
    <T> List<T> searchIndex(String index, Map<String, Object> mustWhere,
                            Map<String, Object> shouldWhere, Map<String, Boolean> sortFieldsToAsc, String[] includeFields,
                            String[] excludeFields, Long timeOut, Class<T> tClass);

}
