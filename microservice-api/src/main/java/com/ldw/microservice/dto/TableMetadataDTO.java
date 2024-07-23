
package com.ldw.microservice.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2021. All rights reserved.
 *
 * @Author Huangy
 * @Date 2021/3/5 11:00
 * @Description 获取元数据表dto
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class TableMetadataDTO extends MetadataDTO implements Serializable {

	/**
	 * serialVersionUID:(序列号).
	 * 
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = 4524603772831991111L;
	/**
	 * @desc 主键id
	 */
	private String id;
	/**
	 * @desc 表名称
	 */
	private String tableName;
	/**
	 * @desc 数据库名称
	 */
	private String databaseName;
	/**
	 * @desc 表描述
	 */
	private String tableComment;
	/**
	 * @desc 表类型：1-物理表
	 */
	private Integer tableType;
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
	@JSONField(format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date createdTime;

	/**
	 * @desc 创建人
	 */
	private String createdBy;

	/**
	 * @desc 更新时间
	 */
	@JSONField(format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date updatedTime;

	/**
	 * @desc 更新人
	 */
	private String updatedBy;
	/**
	 * @desc 字段信息列表
	 */
	private List<ColumnMetadataDTO> columnVOs;
	/**
	 * @desc 关联类型(1: 外部数据源; 2: 计算资源)
	 */
	private Integer relType;
	/**
	 * @desc 多个操作权限以英文逗号分隔
	 */
	private String operationPermissions;
	/**
	 * @desc 表的所有者
	 */
	private String owner;

}
