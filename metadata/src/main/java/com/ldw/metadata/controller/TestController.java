package com.ldw.metadata.controller;

import com.ldw.metadata.collector.OracleCollector;
import com.ldw.metadata.vo.JdbcDatasourceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

/**
 * Copyright © DEEPEXI Technologies Co., Ltd. 2018-2020. All rights reserved.
 *
 * @description:
 * @author: ludanwen
 * @time: 2020/12/7 11:25
 */
@Slf4j
@SuppressWarnings("unchecked")
@Validated
@RestController
@RequestMapping(value = "testController")
public class TestController {

    @Autowired
    OracleCollector oracleCollector;

    @GetMapping("/oracleTest")
    public List oracleTest(@RequestParam("url") String url,
                           @RequestParam("user") String user,
                           @RequestParam("databaseName") String databaseName,
                           @RequestParam("pass") String pass) throws SQLException {
        JdbcDatasourceVO jdbcDatasourceVO;
        jdbcDatasourceVO = new JdbcDatasourceVO();
        final String JDBC_DRIVER = "oracle.jdbc.OracleDriver";
        jdbcDatasourceVO.setDriverClass(JDBC_DRIVER);
        jdbcDatasourceVO.setUrl(url);
        jdbcDatasourceVO.setUsername(user);
        jdbcDatasourceVO.setDatabaseName(databaseName);
        jdbcDatasourceVO.setPassword(pass);
        List taleVo = oracleCollector.collect(jdbcDatasourceVO);
        return taleVo;
    }

}
