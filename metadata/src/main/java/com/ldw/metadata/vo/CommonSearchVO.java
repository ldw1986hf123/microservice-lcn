package com.ldw.metadata.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * ClassName:CommonSearchVO <br/>
 * Function: 通用的查询VO类. <br/>
 * Reason: . <br/>
 * Date: 2020年10月24日 下午6:17:28 <br/>
 * 
 * @author WangXf
 * @version
 * @since JDK 1.8
 * @see
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class CommonSearchVO<T> extends BaseEsVO {

	/**
	 * serialVersionUID:(序列号).
	 * 
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = 374137924492516655L;

	private Integer from;
	private Integer size;
	private Map<String, Object> mustWhere;
	private Map<String, Object> shouldWhere;
	private Map<String, Boolean> sortFieldsToAsc;
	private String[] includeFields;
	private String[] excludeFields;
	private Boolean pagingFlag;
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
	public CommonSearchVO(String index, Integer from, Integer size, Map<String, Object> mustWhere,
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
	public CommonSearchVO(String index, Integer from, Integer size, Map<String, Object> mustWhere,
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

	public CommonSearchVO() {

	}
}
