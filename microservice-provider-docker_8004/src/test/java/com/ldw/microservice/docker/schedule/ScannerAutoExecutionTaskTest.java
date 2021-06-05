package com.ldw.microservice.docker.schedule;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ScannerAutoExecutionTaskTest {

    @Test
    public void execute() throws SQLException {
        String connectUrl="jdbc:mysql://127.0.0.1:3306/metadata";
        Connection conn = DriverManager.getConnection(connectUrl, "root", "aaaaa888");
        System.out.println(conn);
    }

    @Test
    public void interrupt() {
    }
}