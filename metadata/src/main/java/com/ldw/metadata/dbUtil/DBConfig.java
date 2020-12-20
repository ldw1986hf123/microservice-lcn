package com.ldw.metadata.dbUtil;

import com.ldw.metadata.vo.JdbcDatasourceVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * ClassName:DBConfig <br/>
 * Function: 数据库初始化配置信息. <br/>
 * Reason: . <br/>
 * Date: 2020年9月16日 上午11:32:13 <br/>
 * 
 * @author WangXf
 * @version
 * @since JDK 1.8
 * @see
 */
@Slf4j
@Component("commonDBConfig")
public class DBConfig {

	/**
	 * @desc 线程内存块对象
	 */
	private final ThreadLocal<Connection> connLocal = new ThreadLocal<Connection>();

	/**
	 * 
	 * @desc 获取数据库连接
	 * 
	 * @param jdbcDatasourceVO
	 * @return
	 */
	@SuppressWarnings("restriction")
	public Connection getConnection(JdbcDatasourceVO jdbcDatasourceVO) {
		Connection conn = connLocal.get();
		try {
			if (null == conn) {
				//在jdk6中，其实是可以不用调用Class.forName来加载mysql驱动的
				Class.forName(jdbcDatasourceVO.getDriverClass());
				String connectUrl = JdbcConnectionUtils.getUrl(jdbcDatasourceVO);
				if (null != jdbcDatasourceVO.getAuthenticationType() && 1 == jdbcDatasourceVO.getAuthenticationType()) {
					System.setProperty("java.security.krb5.conf", jdbcDatasourceVO.getKrb5ConfPath());
					log.info("日志统一打印 ↓ ↓ ↓ ↓ ↓ ↓ krb5ConfPath = {}", jdbcDatasourceVO.getKrb5ConfPath());
					log.info("日志统一打印 ↓ ↓ ↓ ↓ ↓ ↓ System.getProperty = {}",
							System.getProperty("java.security.krb5.conf"));
					Configuration configuration = new Configuration();
					configuration.set("hadoop.security.authentication", "Kerberos");
					sun.security.krb5.Config.refresh();
					UserGroupInformation.setConfiguration(configuration);
					UserGroupInformation.loginUserFromKeytab(jdbcDatasourceVO.getPrincipal(),
							jdbcDatasourceVO.getKeytabPath());
					if (connectUrl != null && !connectUrl.contains("principal")) {
						connectUrl = connectUrl + ";principal=" + jdbcDatasourceVO.getServicePrincipal();
					}
					conn = DriverManager.getConnection(connectUrl);
				} else {
					System.clearProperty("java.security.krb5.conf");
					conn = DriverManager.getConnection(connectUrl, jdbcDatasourceVO.getUsername(),
							jdbcDatasourceVO.getPassword());
				}
				connLocal.set(conn);
			}
		} catch (Exception e) {
			log.error("failed to got db connection !", e);
			return conn;
		}
		log.info("got db connection successfully !");
		return conn;
	}

	/**
	 * 
	 * @desc 获得普通的连接
	 * 
	 * @param jdbcDatasourceVO
	 * @return
	 */
	public Connection getSimpleConnection(JdbcDatasourceVO jdbcDatasourceVO) {
		Connection conn = null;
		try {
			/*Class.forName(jdbcDatasourceVO.getDriverClass());*/
			String connectUrl = JdbcConnectionUtils.getUrl(jdbcDatasourceVO);
			conn = DriverManager.getConnection(connectUrl, jdbcDatasourceVO.getUsername(),
					jdbcDatasourceVO.getPassword());
			log.info("got db connection successfully !");
		} catch (Exception e) {
			log.error("failed to got db connection !", e);
		}
		return conn;
	}

	/**
	 * 
	 * @desc 释放数据库连接
	 * 
	 * @param dataSourceCode
	 */
	public void release() {
		Connection conn = connLocal.get();
		try {
			if (null != conn && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			log.error("failed to released db connection !", e);
		}
		connLocal.set(null);
		log.info("released db connection successfully !");
	}


	public  void  realeaseResources(PreparedStatement stm, ResultSet resultSet){
		if (null != stm) {
			try {
				stm.close();
			} catch (SQLException e) {
				log.error("关闭 stm 出错", e);
			}
		}


		if (null != resultSet) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				log.error("关闭 resultSet 出错", e);
			}
		}
	}


}
