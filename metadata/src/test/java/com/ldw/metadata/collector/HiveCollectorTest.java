package com.ldw.metadata.collector;


import com.ldw.metadata.vo.JdbcDatasourceVO;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class HiveCollectorTest extends BaseTest {
    @Autowired
    HiveCollector hiveCollector;

    private static Statement statement;
    private static ResultSet resultSet;

    private static String JDBC_DRIVER = "org.apache.hive.jdbc.HiveDriver";
    private static String CONNECTION_URL = "jdbc:hive2://192.168.171.134:10000/";

    private final static String localFilePath = "/home/hadoop/test/hive/test.txt";
    private final static String hdfsFilePath = "hdfs://192.168.171.134:9000/user/hadoop/";
    Connection connection = null;
    JdbcDatasourceVO jdbcDatasourceVO = null;

    @Before
    public void init() {
        jdbcDatasourceVO = new JdbcDatasourceVO();
        jdbcDatasourceVO.setUrl(CONNECTION_URL);
        jdbcDatasourceVO.setDriverClass(JDBC_DRIVER);
        jdbcDatasourceVO.setUsername("root");
        jdbcDatasourceVO.setPassword("aaaaa888");
        jdbcDatasourceVO.setDatabaseName("default");

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
    public void collectTable() throws Exception {
        List list = hiveCollector.getTableMetadata(connection, "default");
        printResult(list);
    }


     /*   #mail
    mail.account = yiyangyu111@163.com
    mail.password = GOZLLKNGYMQBCEQE
    mail.smtp.host = smtp.163.com
    mail.send.milliseconds = 10000
    mail.content.url = http://daas-dev.deepexi.com/daas-management/index.html#/quality/query-task/sheet-details?taskId=taskIdParam
   */

    @Test
    public void collectColumn() throws Exception {
        List<String> tableNameList = new ArrayList<>();
        tableNameList.add("student");
        List list = hiveCollector.getColumnMetadata(connection, tableNameList);
        printResult(list);
    }

    @Test
    public void collectPartition() throws Exception {
    }



    @Test
    public void collect() throws Exception {
        jdbcDatasourceVO.setHdfsUrl("hdfs://192.168.171.134:10000");
        List list = hiveCollector.collect(jdbcDatasourceVO);
        printResult(list);
    }
}
