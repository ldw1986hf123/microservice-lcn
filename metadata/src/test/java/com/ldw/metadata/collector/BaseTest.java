package com.ldw.metadata.collector;

import com.alibaba.fastjson.JSON;
import com.ldw.metadata.vo.JdbcDatasourceVO;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public abstract class BaseTest {
    Statement statement;
    ResultSet resultSet;
    String JDBC_DRIVER = "";
    String CONNECTION_URL = "";
    JdbcDatasourceVO jdbcDatasourceVO = null;

    String databaseName = "";

    @Before
    public abstract void init();


   public void printResult(List list){
       String jsonStr=JSON.toJSONString(list);
       System.out.println(jsonStr);
   }

}
