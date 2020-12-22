package com.ldw.metadata.collector;


import com.ldw.metadata.dbUtil.ConnectUtil;
import com.ldw.metadata.vo.IndexMetadataVO;
import com.ldw.metadata.vo.JdbcDatasourceVO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.util.List;



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
