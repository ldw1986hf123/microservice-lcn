package com.ldw.microservice.comsumer.service.impl;

import com.ldw.microservice.comsumer.dao.DeptDao;
import com.ldw.microservice.comsumer.dao.EmployeeDao;
import com.ldw.microservice.comsumer.service.EmployeeSercice;
import com.ldw.microservice.entity.EmployeeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeSercice {

    @Autowired
    private DeptDao deptDao;

    @Autowired
    private EmployeeDao employeeDao;


    public boolean addEmployee(EmployeeInfo employeeInfo) {
        return employeeDao.addEmployee(employeeInfo);
    }

    public EmployeeInfo findById(Long id) {
      EmployeeInfo employeeInfo=employeeDao.findById(id);
      return  employeeInfo;
    }

    public List findAll() {
       List list=employeeDao.findAll();
       return  list;
    }


}
