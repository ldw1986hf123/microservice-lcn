
package com.ldw.microservice.docker.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;


/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description: 字段元数据
 * @author: wenggufiang
 * @time: 2021年3月18日11:30:51
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class MetadataColumnDTO implements Serializable {

	/**
	 * serialVersionUID:(序列号).
	 * 
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = -7039620361961514227L;
	/**
	 * 表ID
	 */
	private Long tableId;

	/**
	 * 数据类型
	 */
	private String dataType;

	/**
	 * 字段名称
	 */
	private String code;

	/**
	 * 字段描述
	 */
	private String name;

	/**
	 * 是否非空(0：否，1：是)
	 */
	private Integer isRequired;

	/**
	 * 是否为主键(0：否，1：是)
	 */
	private Integer isPrimaryKey;

	/**
	 * 是否为外键(0：否，1：是)
	 */
	private Integer isForeignKey;

	/**
	 * 是否分区字段(0：否，1：是)
	 */
	private Integer isPartitionKey;

	/**
	 * 0-唯一约束；1-默认约束；2-主键约束；3-外键约束；4-CHECK约束；5-唯一约束(oracle)；6-主键约束(oracle)；7-外键约束(oracle)；8-视图约束；9-只读约束；10-CHECK约束(sqlserver)；11-值不为空(sqlserver)；（多个约束使用逗号隔开）
	 */
	private String columnConstraint;

	/**
	 * 长度
	 */
	private Integer length;

	/**
	 * 精度
	 */
	private Integer precision;

	/**
	 * 默认值
	 */
	private String defaultValue;

	/**
	 * 字段压缩算法，kudu库专用字段
	 */
	private String compressionAlgorithm;

	/**
	 * 字段编码，kudu库专用字段
	 */
	private String encoding;

	/**
	 * 字段显示次序号
	 */
	private Integer sortNo;

	/**
	 * @desc 表名称
	 */
	private String tableCode;

	/**
	 * 约束字符串
	 */
	private String ConstraintsStr;

	/**
	 * 元数据接收字段
	 */
	private String isNull;


}
