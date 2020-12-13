package com.ldw.metadata.vo;

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

	@Override
	public Long getId() {

		return this.id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	@Override
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	@Override
	public Long getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Integer getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(Integer authenticationType) {
		this.authenticationType = authenticationType;
	}

	public String getKrb5ConfPath() {
		return krb5ConfPath;
	}

	public void setKrb5ConfPath(String krb5ConfPath) {
		this.krb5ConfPath = krb5ConfPath;
	}

	public String getKeytabPath() {
		return keytabPath;
	}

	public void setKeytabPath(String keytabPath) {
		this.keytabPath = keytabPath;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getServicePrincipal() {
		return servicePrincipal;
	}

	public void setServicePrincipal(String servicePrincipal) {
		this.servicePrincipal = servicePrincipal;
	}

	@Override
	public String toString() {
		return "JdbcDatasourceVO{" +
				"id=" + id +
				", code='" + code + '\'' +
				", url='" + url + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", databaseName='" + databaseName + '\'' +
				", driverClass='" + driverClass + '\'' +
				", type='" + type + '\'' +
				", tenantId='" + tenantId + '\'' +
				", projectId=" + projectId +
				", authenticationType=" + authenticationType +
				", krb5ConfPath='" + krb5ConfPath + '\'' +
				", keytabPath='" + keytabPath + '\'' +
				", principal='" + principal + '\'' +
				", servicePrincipal='" + servicePrincipal + '\'' +
				'}';
	}
}
