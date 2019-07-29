package com.ldw.microservice.comsumer.service.impl;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.ldw.microservice.comsumer.dao.DeptDao;
import com.ldw.microservice.comsumer.service.DeptFeignService;
import com.ldw.microservice.comsumer.service.DeptSercice;
import com.ldw.microservice.entity.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptSercice {

    @Autowired
    private DeptDao deptDao;

    @Autowired
    private DeptFeignService deptFeignService;

    public boolean addDpet(Dept dept) {
        deptDao.addDpet(dept);
        return  false;
    }

    public Dept findById(Long id) {
        return deptDao.findById(id);
    }

    @SuppressWarnings("rawtypes")
    public List findAll() {
        return deptDao.findAll();
    }



    @LcnTransaction
    public void lcnTxTestAdd(Dept dept) {
        deptDao.addDpet(dept);
        Dept dept2 = new Dept();
        dept2.setDName("db2");
        dept2.setDeptNo(System.currentTimeMillis());
        int a=3/0;
        deptFeignService.addDept(dept2);
    }


}
