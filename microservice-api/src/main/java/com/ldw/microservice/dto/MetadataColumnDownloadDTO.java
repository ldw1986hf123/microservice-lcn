package com.ldw.microservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * ClassName:ColumnMetadataDownloadVO <br/>
 * Function: 元数据下载VO对象. <br/>
 * Reason: . <br/>
 * Date: 2020年11月6日 下午5:33:14 <br/>
 * 
 * @author WangXf
 * @version
 * @since JDK 1.8
 * @see
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class MetadataColumnDownloadDTO {

	/**
	 * @desc 字段名称
	 */
	private String code;
	/**
	 * @desc 字段描述
	 */
	private String name;
	/**
	 * @desc 字段类型
	 */
	private String dataType;
	/**
	 * @desc 是否是主键
	 */
	private String isPrimaryKey;
	/**
	 * @desc 是否是外键
	 */
	private String isForeignKey;
	/**
	 * @desc 字段约束
	 */
	private String columnConstraint;
}
