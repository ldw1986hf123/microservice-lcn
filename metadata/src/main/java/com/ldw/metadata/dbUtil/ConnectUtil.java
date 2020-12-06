package com.ldw.metadata.dbUtil;


import java.sql.*;
import javax.sql.*;

public class ConnectUtil {
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL = "jdbc:sqlserver://localhost:1433;databasename=";
    private static final String DATABASE_NAME = "ldw_test";
    private static final String USER_NAME = "sa";
    private static final String PASSWORD = "aaaaa888";


    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL + DATABASE_NAME, USER_NAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


}
