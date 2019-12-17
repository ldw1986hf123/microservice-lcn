package com.ldw.microservice.dao;


import com.ldw.microservice.DeptProvider8002_App;
import com.ldw.microservice.entity.AnchorOrderSummary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@SpringBootTest(classes = DeptProvider8002_App.class)
@RunWith(SpringRunner.class)
/**  指定当前生效的配置文件( active profile)，如果是 appplication-dev.yml 则 dev   **/
@ActiveProfiles("test")
/** 指定  @SpringBootApplication  启动类 和 端口  **/
public class AnchorOrderSummaryDaoTest {

    @Autowired
    AnchorOrderSummaryDao anchorOrderSummaryDao;


    @Test
    public void insert() {
        AnchorOrderSummary anchorOrderSummary = new AnchorOrderSummary();
        anchorOrderSummary.setActualAmount(new BigDecimal(0.0));
        anchorOrderSummary.setAnchorAmount(new BigDecimal(2.0));
        anchorOrderSummary.setCurrency("eos");
        anchorOrderSummary.setAnchorCurrency("eoss");
        anchorOrderSummaryDao.addRecord(anchorOrderSummary);
    }

    @Test
    public void findById() {
        System.out.println(anchorOrderSummaryDao.findById(4));
    }
}