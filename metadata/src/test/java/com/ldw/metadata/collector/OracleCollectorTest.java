package com.ldw.metadata.collector;


import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import com.ldw.metadata.MetadataApplication;
import com.ldw.metadata.dbUtil.ConnectUtil;
import com.ldw.metadata.dbUtil.DBUtils;
import com.ldw.metadata.vo.IndexMetadataVO;
import com.ldw.metadata.vo.JdbcDatasourceVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class OracleCollectorTest {

    @Autowired
    OracleCollector oracleCollector;

    @Test
    public void getIndexMetadata() {
        Connection connection = null;
        JdbcDatasourceVO jdbcDatasourceVO = new JdbcDatasourceVO();
        jdbcDatasourceVO.setUrl("jdbc:oracle:thin:@//192.168.171.134:49161/XE");
        jdbcDatasourceVO.setUsername("system");
        jdbcDatasourceVO.setPassword("oracle");
        jdbcDatasourceVO.setDriverClass("oracle.jdbc.driver.OracleDriver");
        try {
            connection = ConnectUtil.getConnection(jdbcDatasourceVO);
            List<IndexMetadataVO> indexMetadataVOS = oracleCollector.getIndexMetadata(connection);

            indexMetadataVOS.forEach(
                    vo ->{
                        System.out.println(vo);
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
