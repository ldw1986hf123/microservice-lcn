package com.ldw.microservice.dao;

import com.ldw.microservice.entity.OperLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OperLogMapper {
    int insert(Map mapParam);

    int insertSelective(OperLog mapParam);


    List getPage(OperLog mapParam);
}