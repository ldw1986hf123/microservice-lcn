package com.ldw.microservice.dao;

import com.ldw.microservice.DeptProvider8002_App;
import com.ldw.microservice.entity.OperLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@SpringBootTest(classes = DeptProvider8002_App.class)
@RunWith(SpringRunner.class)
/**  指定当前生效的配置文件( active profile)，如果是 appplication-dev.yml 则 dev   **/
@ActiveProfiles("test")
/** 指定  @SpringBootApplication  启动类 和 端口  **/
public class OperLogMapperTest {

    @Autowired
    OperLogMapper operLogMapper;

    @Test
    public void insert() {
    }

    @Test
    public void insertSelective() {
    }

    @Test
    public void getPage() {
    }

    @Test
    public void getByCondition() {

        OperLog operLog = new OperLog();
        operLog.setNoted("1");
        List existedList = operLogMapper.getByCondition(operLog);
        Map receivedMap = new HashMap();
        receivedMap.put("banner1", "232");
        receivedMap.put("banner2", "asd");

    }


}