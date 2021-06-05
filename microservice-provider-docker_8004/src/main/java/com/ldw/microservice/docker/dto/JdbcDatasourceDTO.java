package com.ldw.microservice.docker.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description: Jdbc数据源实现类
 * @author: wenggufiang
 * @time: 2021年3月18日11:30:51
 */
@ToString
@Data
public class JdbcDatasourceDTO {
	/**
	 * serialVersionUID:(序列号).
	 * 
	 * @since JDK 1.8
	 */
	private static final long serialVersionUID = 2247865675847143928L;
	/**
	 * @desc 数据源Id
	 */
	private Long id;
	/**
	 * @desc 数据源代码
	 */
	private String code;
	/**
	 * @desc 数据源地址
	 */
	private String url;
	/**
	 * @desc 用户名
	 */
	private String username;
	/**
	 * @desc 密码
	 */
	private String password;
	/**
	 * @desc 数据库名称
	 */
	private String databaseName;
	/**
	 * @desc 数据库驱动类
	 */
	private String driverClass;
	/**
	 * @desc 数据源类型
	 */
	private String type;
	/**
	 * @desc 租户Id
	 */
	private String tenantId;
	/**
	 * @desc 项目Id
	 */
	private Long projectId;

	/**
	 * @desc认证类型(0：NONE, 1:Kerberos)
	 */
	private Integer authenticationType;
	/**
	 * @desc krb5配置文件路径
	 */
	private String krb5ConfPath;
	/**
	 * @desc keytab文件路径
	 */
	private String keytabPath;
	/**
	 * @desc 认证账号
	 */
	private String principal;

	/**
	 * @desc 个人认证账号
	 */
	private String servicePrincipal;

	/**
	 *  `rel_type` tinyint(4) DEFAULT '2' COMMENT '关联类型(1: 外部数据源; 2: 计算资源)'
	 */
	private Integer relType;

	/**
	 * 环境类型（开发0、生产1、基础环境2）',
	 */
	private Integer env;

	/**
	 * '环境类型（开发0、生产1、基础环境2）',
	 */
	public enum ENV {
		DEV(0, "dev"),
		PRODUCT(1, "product"),
		BASIC(2, "basic");

		private Integer value;
		private String name;

		ENV(Integer value, String name) {
			this.value = value;
			this.name = name;
		}

		public Integer getValue() {
			return value;
		}

		public String getName() {
			return name;
		}

		public static String getEnumType(String value) {
			ENV[] alarmGrades = ENV.values();
			for (int i = 0; i < alarmGrades.length; i++) {
				if (alarmGrades[i].getValue().equals(value)) {
					return alarmGrades[i].name;
				}
			}
			return "";
		}
	}
}
