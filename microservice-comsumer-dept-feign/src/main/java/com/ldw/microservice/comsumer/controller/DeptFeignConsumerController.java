package com.ldw.microservice.comsumer.controller;

import com.ldw.microservice.comsumer.service.DeptSercice;
import com.ldw.microservice.entity.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeptFeignConsumerController {

    // private final String REST_URL_PREX = "http://localhost:8001/";
    // private final String REST_URL_PREX = "http://MICROSERVICE-DEPT/";


    @Autowired
    private DeptSercice deptSercice;

    @RequestMapping("/comsumer/dept/add")
    public boolean addDept(Dept dept) {
        System.out.println("dept :" + dept);
        return deptSercice.addDpet(dept);
    }

    @RequestMapping("/comsumer/dept/lcnTxTestAdd")
    public void lcnTxTestAdd() {
        Dept dept1 = new Dept();
        dept1.setDeptNo(System.currentTimeMillis());
        dept1.setDName("aa");
        deptSercice.lcnTxTestAdd(dept1);
    }


    @RequestMapping("/comsumer/dept/get/{id}")
    public Dept get(@PathVariable("id") Long id) {
        System.out.println("comsumer get");
        Dept dept = deptSercice.findById(id);
        return dept;
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping("/comsumer/dept/list")
    public List list() {
        System.out.println("comsumer list");
        return deptSercice.findAll();
    }
}
