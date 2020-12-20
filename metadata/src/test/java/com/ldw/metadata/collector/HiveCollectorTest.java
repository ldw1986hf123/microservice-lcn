package com.ldw.metadata.collector;


import com.ldw.metadata.vo.JdbcDatasourceVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.*;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class HiveCollectorTest {
    @Autowired
    HiveCollector hiveCollector;

    String tableName = "  student  ";
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
        jdbcDatasourceVO=new JdbcDatasourceVO();
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
    public void connectHive() throws SQLException {
        System.out.println("通过JDBC连接非Kerberos环境下的HiveServer2");

        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("select * from student");
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "-------" + rs.getString(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
            ps.close();
            rs.close();
        }
    }

    @Test
    public void connectHiveWithKerberos() throws SQLException {
        //登录Kerberos账号
     /*   System.setProperty("java.security.krb5.conf", "/Volumes/Transcend/keytab/krb5.conf");
        Configuration configuration = new Configuration();
        configuration.set("hadoop.security.authentication" , "Kerberos" );
        UserGroupInformation. setConfiguration(configuration);
        UserGroupInformation.loginUserFromKeytab("fayson@CLOUDERA.COM", "/Volumes/Transcend/keytab/fayson.keytab");

        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            connection = DriverManager.getConnection(CONNECTION_URL);
            ps = connection.prepareStatement("select * from test_table");
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.disconnect(connection, rs, ps);
        }*/
    }


    @Test
    public void createTable() throws SQLException {
        String sql = "drop table " + tableName;
        System.out.println("delete table****");
        statement.execute(sql);
        sql = "create table " + tableName + " (key int,value String)"
                + " row format delimited fields terminated by '\t'";
        System.out.println("create table:" + tableName);
        statement.execute(sql);
        showTable();
        describeTable();
    }

    @Test
    public void describeTable() throws SQLException {
        String sql = "describe " + tableName;
        System.out.println("describe table:" + tableName);
        resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1) + "\t" + resultSet.getString(2));
        }
    }

    @Test
    public void showTable() throws SQLException {
        String sql = "show tables " + tableName;
        System.out.println("show table:" + tableName);
        resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1));
        }
    }

    @Test
    public void loadDataToTable() throws SQLException {
    /*    String sql = isLocal ? "load data local inpath '" + localFilePath + "' overwrite into table " + tableName :
                "load data inpath '" + hdfsFilePath + "' overwrite into table " + tableName;
        System.out.println("load data into table:" + tableName);
        statement.executeQuery(sql);*/
    }

    @Test
    public void collectTable() {

        List list = hiveCollector.collect(jdbcDatasourceVO);
        for (Object o : list) {
            System.out.println(o);
        }
    }

    public static void createPartition() throws SQLException{
        String partitionName="stu_par_1 ";
        String    sql="drop table "+partitionName;
        System.out.println("delete partition");
        statement.execute(sql);
        sql="create table "+partitionName+"(key int) partitioned by (value string) "
                + "row format delimited fields terminated by '\t'";
        System.out.println("create partition:"+partitionName);
        statement.execute(sql);
    }



    public   void insertDataToPartition() throws SQLException{
        String insterSQL="LOAD DATA LOCAL INPATH '/ldw/add.txt' OVERWRITE INTO TABLE javabloger";
        Statement stmt = connection.createStatement();
        stmt.executeQuery(insterSQL);  // 执行插入语句
    }


}
