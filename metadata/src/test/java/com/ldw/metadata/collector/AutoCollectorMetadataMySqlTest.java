package com.ldw.metadata.collector;

import com.ldw.metadata.constant.CommonConstants;
import com.ldw.metadata.vo.JdbcDatasourceVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.Assert.*;

public class AutoCollectorMetadataMySqlTest extends BaseTest {
    @Autowired
    AutoCollectorMetadataMySql autoCollectorMetadataMySql;

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "aaaaa888";
    String databaseName = "luckypai";

    Connection connection = null;

    @Override
    public void init() {
        jdbcDatasourceVO = new JdbcDatasourceVO();
        jdbcDatasourceVO.setUrl(URL);
        jdbcDatasourceVO.setDriverClass(DRIVER);
        jdbcDatasourceVO.setUsername(USER_NAME);
        jdbcDatasourceVO.setPassword(PASSWORD);
        jdbcDatasourceVO.setDatabaseName(databaseName);
        jdbcDatasourceVO.setType(CommonConstants.DataSourceType.MYSQL);
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
        try {
            connection = DriverManager.getConnection(CONNECTION_URL, "root", "aaaaa888"); //首先要打开hiveserver服务：hive  --service hiveserver
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void collect() {
        List list = autoCollectorMetadataMySql.collect(jdbcDatasourceVO);
        printResult(list);
    }


}