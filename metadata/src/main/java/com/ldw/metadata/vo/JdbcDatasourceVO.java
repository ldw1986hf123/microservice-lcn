package com.ldw.metadata.vo;

import lombok.Data;
import lombok.ToString;

/**
 * ClassName:JdbcDatasourceVO <br/>
 * Function: Jdbc数据源VO实现类. <br/>
 * Reason: . <br/>
 * Date: 2020年9月17日 下午5:06:44 <br/>
 * 
 * @author WangXf
 * @version
 * @since JDK 1.8
 * @see
 */
@Data
@ToString
public class JdbcDatasourceVO extends DatasourceVO {
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

	private String hdfsUrl;

}
