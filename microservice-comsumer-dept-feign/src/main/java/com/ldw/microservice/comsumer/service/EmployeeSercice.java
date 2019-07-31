package com.ldw.microservice.comsumer.service;

import com.ldw.microservice.entity.Dept;
import com.ldw.microservice.entity.EmployeeInfo;

import java.util.List;

public interface EmployeeSercice {
    boolean addEmployee(EmployeeInfo employeeInfo);

    EmployeeInfo findById(Long id);

    @SuppressWarnings("rawtypes")
    List findAll();

}
