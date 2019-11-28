package com.ldw.microservice.service.impl;

import com.github.pagehelper.PageHelper;
import com.ldw.microservice.annotation.ExtPageHelper;
import com.ldw.microservice.dao.OperLogMapper;
import com.ldw.microservice.entity.OperLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OperLogServiceImpl {

    @Autowired
    private OperLogMapper operLogMapper;

    public int insert(Map mapParam) {
        return operLogMapper.insert(mapParam);
    }

    public int insertSelective(OperLog operLog) {
        return operLogMapper.insertSelective(operLog);
    }

    public List getPage(OperLog noted) {
        return operLogMapper.getPage(noted);
    }


//    @ExtPageHelper
    public Object getPageWithAnnotation(OperLog noted) {
        PageHelper.startPage(1, 10);
        OperLog param = new OperLog();
        param.setNoted("3");
        return operLogMapper.getPage(noted);
    }

}
