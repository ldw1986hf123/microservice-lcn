package com.ldw.microservice.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 *
 * @Author 卢丹文
 * @Date 2021/3/4 14:13
 * @Description
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class CommonSearchQuery<T> extends BaseEsQuery{

    @ApiModelProperty(value = "开始查询位置", example = "1")
    private Integer from;

    @ApiModelProperty(value = "查询多少", example = "10")
    private Integer size;

    @ApiModelProperty(value = "查询条件")
    private Map<String, Object> mustWhere;

    @ApiModelProperty(value = "查询条件shouldWhere")
    private Map<String, Object> shouldWhere;

    @ApiModelProperty(value = "排序条件")
    private Map<String, Boolean> sortFieldsToAsc;

    @ApiModelProperty(value = "查询字段", example = "id")
    private String[] includeFields;

    @ApiModelProperty(value = "排除字段")
    private String[] excludeFields;

    @ApiModelProperty(value = "是否分页", example = "true")
    private Boolean pagingFlag;

    @ApiModelProperty(value = "返回的类class")
    private Class<T> classObject;

    /**
     * Creates a new instance of CommonSearchVO.
     *
     * @param index           索引
     * @param from            当前页
     * @param size            每页显示条数
     * @param mustWhere       and查询条件
     * @param shouldWhere     or查询条件
     * @param sortFieldsToAsc 排序字段列表
     * @param includeFields   结果返回字段列表
     * @param excludeFields   结果不返回字段列表
     * @param timeOut         查询超时
     * @param pagingFlag      是否分页
     */
    public CommonSearchQuery(String index, Integer from, Integer size, Map<String, Object> mustWhere,
                          Map<String, Object> shouldWhere, Map<String, Boolean> sortFieldsToAsc, String[] includeFields,
                          String[] excludeFields, Long timeOut, Boolean pagingFlag) {
        this.setIndex(index);
        this.from = from;
        this.size = size;
        this.mustWhere = mustWhere;
        this.shouldWhere = shouldWhere;
        this.sortFieldsToAsc = sortFieldsToAsc;
        this.includeFields = includeFields;
        this.excludeFields = excludeFields;
        this.setTimeout(timeOut);
        this.pagingFlag = pagingFlag;
    }

    /**
     * Creates a new instance of CommonSearchVO.
     *
     * @param index           索引
     * @param from            当前页
     * @param size            每页显示条数
     * @param mustWhere       and查询条件
     * @param shouldWhere     or查询条件
     * @param sortFieldsToAsc 排序字段列表
     * @param includeFields   结果返回字段列表
     * @param excludeFields   结果不返回字段列表
     * @param timeOut         查询超时
     * @param pagingFlag      是否分页
     * @param classObject     类对象
     */
    public CommonSearchQuery(String index, Integer from, Integer size, Map<String, Object> mustWhere,
                          Map<String, Object> shouldWhere, Map<String, Boolean> sortFieldsToAsc, String[] includeFields,
                          String[] excludeFields, Long timeOut, Boolean pagingFlag, Class<T> classObject) {
        this.setIndex(index);
        this.from = from;
        this.size = size;
        this.mustWhere = mustWhere;
        this.shouldWhere = shouldWhere;
        this.sortFieldsToAsc = sortFieldsToAsc;
        this.includeFields = includeFields;
        this.excludeFields = excludeFields;
        this.setTimeout(timeOut);
        this.pagingFlag = pagingFlag;
        this.classObject = classObject;
    }

    public CommonSearchQuery() {

    }

}
