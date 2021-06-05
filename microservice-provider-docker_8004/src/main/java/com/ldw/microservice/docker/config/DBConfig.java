package com.ldw.microservice.docker.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description: 数据库初始化配置信息
 * @author: wengguifang
 * @time: 2021年3月18日11:50:39
 */
@Slf4j
@Component("commonDBConfig")
public class DBConfig {

    /**
     * @desc 线程内存块对象
     */
    private final ThreadLocal<Connection> connLocal = new ThreadLocal<Connection>();

    /**
     * @param jdbcDatasourceDTO
     * @return
     * @desc 获得普通的连接
     */
    public Connection getSimpleConnection(String url,String username,String password) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username,password);
            log.info("got db connection successfully !");
        } catch (Exception e) {
            log.error("failed to got db connection !", e);
        }
        return conn;
    }

    /**
     * @desc 释放数据库连接
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

    public void realeaseResources(PreparedStatement stm, ResultSet resultSet) {
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
//	/**
//	 *
//	 * @desc 获得url
//	 *
//	 * @param jdbcDatasourceVO
//	 * @return
//	 */
//	private String getUrl(JdbcDatasourceVO jdbcDatasourceVO) {
//		StringBuilder stringBuilder = new StringBuilder();
//		if (CommonConstants.DataSourceType.IMPALA.equals(jdbcDatasourceVO.getType())) {
//			if (jdbcDatasourceVO.getUrl().endsWith("/")) {
//				stringBuilder.append(jdbcDatasourceVO.getUrl()).append(jdbcDatasourceVO.getDatabaseName());
//			} else {
//				stringBuilder.append(jdbcDatasourceVO.getUrl()).append("/").append(jdbcDatasourceVO.getDatabaseName());
//			}
//			stringBuilder.append(";UseSasl=0;AuthMech=0;UID=").append(jdbcDatasourceVO.getUsername()).append(";PWD=")
//					.append(jdbcDatasourceVO.getPassword()).append(";");
//		} else {
//			if (jdbcDatasourceVO.getUrl().endsWith("/")) {
//				stringBuilder.append(jdbcDatasourceVO.getUrl()).append(jdbcDatasourceVO.getDatabaseName());
//			} else {
//				stringBuilder.append(jdbcDatasourceVO.getUrl()).append("/").append(jdbcDatasourceVO.getDatabaseName());
//			}
//		}
//		return stringBuilder.toString();
//
//	}

}
