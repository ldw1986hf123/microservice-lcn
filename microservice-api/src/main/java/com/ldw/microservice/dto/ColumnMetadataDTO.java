
package com.ldw.microservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/4 11:00
 * @Description 元数据字段dto
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class ColumnMetadataDTO extends MetadataDTO implements Serializable {

	/**
	 * serialVersionUID:(序列号).
	 * 
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = -7039620361961514227L;
	/**
	 * @desc 主键id
	 */
	private String id;
	/**
	 * @desc 字段名称
	 */
	private String columnName;
	/**
	 * @desc 字段类型
	 */
	private String columnType;
	/**
	 * @desc 字段约束
	 */
	private String columnConstraint;
	/**
	 * @desc 字段描述
	 */
	private String columnComment;
	/**
	 * @desc 是否为空
	 */
	private Boolean isNull;
	/**
	 * @desc 是否是主键
	 */
	private Boolean isPrimaryKey;
	/**
	 * @desc 长度
	 */
	private Integer length;
	/**
	 * @desc 精度
	 */
	private Integer precision;
	/**
	 * @desc 数据源Id
	 */
	private Long dataSourceId;
	/**
	 * @desc 数据源类型：mysql-mysql；oracle-oracle；spark-spark；sqlserver-sqlserver；postgresql-postgresql；kylin-kylin；ftp-ftp；hive-hive；hdfs-hdfs；hbase-hbase；es-es；impala-impala；kafka-kafka；kudu-kudu
	 */
	private String dataSourceType;
	/**
	 * @desc 数据源名称
	 */
	private String dataSourceName;
	/**
	 * @desc 数据源环境类型（开发0、生产1、基础环境2）
	 */
	private Integer dataSourceEnv;
	/**
	 * @desc 项目Id
	 */
	private Long projectId;
	/**
	 * @desc 住户id
	 */
	private String tenantId;
	/**
	 * @desc 乐观锁版本号
	 */
	private Integer version;

	/**
	 * @desc 删除状态 0无效 1有效
	 */
	private Integer isDeleted;

	/**
	 * @desc 创建时间
	 */
	private Date createdTime;

	/**
	 * @desc 创建人
	 */
	private String createdBy;

	/**
	 * @desc 更新时间
	 */
	private Date updatedTime;

	/**
	 * @desc 更新人
	 */
	private String updatedBy;
	/**
	 * @desc 表元数据id
	 */
	private String tableMetadataId;
	/**
	 * @desc 关联类型(1: 外部数据源; 2: 计算资源)
	 */
	private Integer relType;
	/**
	 * @desc 默认值
	 */
	private String defaultValue;
	/**
	 * @desc 字段编码，kudu库专用字段
	 */
	private String encoding;
	/**
	 * @desc 字段压缩算法，kudu库专用字段
	 */
	private String compressionAlgorithm;
	/**
	 * @desc 字段显示次序号
	 */
	private Integer orderNo;

}
