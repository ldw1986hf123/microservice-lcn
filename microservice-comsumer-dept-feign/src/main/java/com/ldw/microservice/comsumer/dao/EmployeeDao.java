package com.ldw.microservice.comsumer.dao;

import com.ldw.microservice.entity.Dept;
import com.ldw.microservice.entity.EmployeeInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeDao {
    boolean addEmployee(EmployeeInfo employeeInfo);

    EmployeeInfo findById(Long id);

    List findAll();
}
