
package com.ldw.microservice.docker.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description: 表元数据
 * @author: wenggufiang
 * @time: 2021年3月18日11:30:51
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class MetadataTableDTO  implements Serializable {

	/**
	 * serialVersionUID:(序列号).
	 * 
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = 4524603772831991111L;
	/**
	 * 数据源类型：mysql-mysql；oracle-oracle；sqlserver-sqlserver；hive-hive；impala-impala；kafka-kafka；kudu-kudu
	 */
	private String datasourceType;

	/**
	 * 数据源ID
	 */
	private Long datasourceId;

	/**
	 * 关联类型(1: 外部数据源; 2: 计算资源)
	 */
	private Integer relType;

	/**
	 * 数据源环境类型（0: 开发、1: 生产、2: 基础环境）
	 */
	private Integer datasourceEnv;

	/**
	 * 数据库名称
	 */
	private String databaseName;

	/**
	 * 项目Id
	 */
	private Long projectId;

	/**
	 * 表名
	 */
	private String code;

	/**
	 * 表描述
	 */
	private String name;

	/**
	 * 表的所有者
	 */
	private String owner;

	/**
	 * 操作权限。oracle跨库采集元数据，多个权限使用逗号隔开，如：SELECT
	 */
	private String operationPermissions;

	/**
	 * 版本号
	 */
	private Integer versionNumber;
	/**
	 * @desc 字段信息列表
	 */
	private List<MetadataColumnDTO> columnDTOS;
	/**
	 * @desc 索引列表
	 */
	private List<MetadataIndexDTO> indexDTOS;

	/**
	 * @desc 分区列表
	 */
	private List<MetadataPartitionDTO> partitionDTOS;

	/**
	 * 纯净的表名，没有前缀，作为获取分区和索引等使用
	 */
	private String pureTableName;

	/**
	 * 数据源名称
	 */
	private String datasourceName;


}
