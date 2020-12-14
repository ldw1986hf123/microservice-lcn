package com.ldw.metadata.controller;

import com.ldw.metadata.dbUtil.HttpClientUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestControllerTest {

    String url = "http://localhost:8125/testController/oracleTest";

    @Test
    public void oracleTest() {

        Map param = new HashMap();
        param.put("url", "jdbc:oracle:thin:@//192.168.171.134:49161");
        param.put("user", "system");
        param.put("pass", "oracle");
        param.put("databaseName", "oracle");
        System.out.println(HttpClientUtil.sendGet(param, url));

    }
}