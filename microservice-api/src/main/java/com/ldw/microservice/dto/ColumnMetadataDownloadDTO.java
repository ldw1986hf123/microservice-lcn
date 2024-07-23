package com.ldw.microservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/3 11:00
 * @Description 影响分析下载dto
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class ColumnMetadataDownloadDTO {

	/**
	 * @desc 字段名称
	 */
	private String columnName;
	/**
	 * @desc 字段类型
	 */
	private String columnType;
	/**
	 * @desc 字段描述
	 */
	private String columnComment;
	/**
	 * @desc 是否是主键
	 */
	private String isPrimaryKey;
}
