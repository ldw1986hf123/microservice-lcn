package com.ldw.metadata.vo.dbUtil;


import com.ldw.metadata.dbUtil.ConnectUtil;
import com.ldw.metadata.dbUtil.DBUtils;
import com.ldw.metadata.vo.ColumnMetadataVO;
import org.junit.Test;

import java.sql.*;
import java.util.List;

public class ConnectUtilTest {
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL = "jdbc:sqlserver://localhost:1433;databasename=";
    private static final String DATABASE_NAME = "ldw_test";
    private static final String USER_NAME = "sa";
    private static final String PASSWORD = "aaaaa888";

    @Test
    public void getColumnMetadata() {
        Connection connection = null;
        try {
            connection = ConnectUtil.getConnection();
            //新建一个查询
            Statement stmt = connection.createStatement();
            //执行查询-->>返回一个结果集
            ResultSet rs = stmt.executeQuery("select  colunm_name as  columnName, colunm_type as columnType,comment as comment  from colunm_metadata");    //括号里可以写相关的SQL语句，并把查询到的所有，放到一个rs集合里
            List list = DBUtils.convertList(rs, ColumnMetadataVO.class);

            list.forEach(item -> {
                System.out.println(item);
            });


            //这这是一个逻辑需求，如果数据库没有需要查找的内容，那么这句话怎么说都比白屏好看
            rs.close();
            stmt.close();//这三行是关闭连接的意思，这非常重要，如果没写关闭连接
            connection.close();//程序多人打开或多人访问，就会出现卡顿，重启或奔溃
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Test
    public void getTableMetadata() {
        Connection connection = null;
        try {
            connection = ConnectUtil.getConnection();
            //新建一个查询
            Statement stmt = connection.createStatement();
            //执行查询-->>返回一个结果集
            ResultSet rs = stmt.executeQuery("select  colunm_name as  columnName, colunm_type as columnType,comment as comment  from colunm_metadata");    //括号里可以写相关的SQL语句，并把查询到的所有，放到一个rs集合里
            List list = DBUtils.convertList(rs, ColumnMetadataVO.class);

            list.forEach(item -> {
                System.out.println(item);
            });
            //这这是一个逻辑需求，如果数据库没有需要查找的内容，那么这句话怎么说都比白屏好看
            rs.close();
            stmt.close();//这三行是关闭连接的意思，这非常重要，如果没写关闭连接
            connection.close();//程序多人打开或多人访问，就会出现卡顿，重启或奔溃
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Test
    public void insertData() {
        Connection connection = null;
        try {
            connection = ConnectUtil.getConnection();
            //执行查询-->>返回一个结果集

            String sql = "insert into colunm_metadata (id,colunm_name,colunm_type,length,comment) values (?,?,?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setInt(1, 3);
            pstm.setString(2, "dasd");
            pstm.setString(3, "int");
            pstm.setInt(4, 18);
            pstm.setString(5, "注释2");
            pstm.executeUpdate();
            System.out.println("插入成功...");

            //这这是一个逻辑需求，如果数据库没有需要查找的内容，那么这句话怎么说都比白屏好看
            pstm.close();//这三行是关闭连接的意思，这非常重要，如果没写关闭连接
            connection.close();//程序多人打开或多人访问，就会出现卡顿，重启或奔溃
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void printByMetadata() throws Exception {
        Connection con = ConnectUtil.getConnection();
        String sql = "select * from colunm_metadata";
        PreparedStatement pstat = con.prepareStatement(sql);
        ResultSetMetaData rsmd = pstat.getMetaData();//获取结果集元数据
        //DatabaseMetaData dbmd = con.getMetaData();
        int count = rsmd.getColumnCount(); //获取结果集元数据列数
        System.out.println("表一共有：" + count + "列");
        //遍历属性名称
        for (int i = 1; i <= count; i++) {
            System.out.println("第" + i + "行的属性为：" + rsmd.getColumnName(i)
                    + "，类型为：" + rsmd.getColumnTypeName(i));
        }
    }


}
