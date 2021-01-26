package com.ldw.microservice.comsumer.controller;

import java.util.List;

import com.ldw.microservice.comsumer.service.DeptSercice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ldw.microservice.entity.Dept;

@RestController
public class DeptComsumerController {

    // private final String REST_URL_PREX = "http://localhost:8001/";
    private final String REST_URL_PREX = "http://MICROSERVICE-DEPT/"; // 通过微服务名称来访问才是最标准的，不用关心IP地址，端口等信息

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    DeptSercice deptSercice;

    @RequestMapping("/comsumer/dept/add")
    public boolean addDept(Dept dept) {
        System.out.println("dept :" + dept);
        return restTemplate.postForObject(REST_URL_PREX + "dept/add", dept,
                Boolean.class);

    }

    @RequestMapping("/comsumer/dept/get/{id}")
    public Dept get(@PathVariable Long id) {
        System.out.println("comsumer get");
        return restTemplate.getForObject(REST_URL_PREX + "dept/get/" + id,
                Dept.class);

    }

    @RequestMapping("/dept/list")
    public List RestTemplattelist() {
        System.out.println("comsumer list");
        return restTemplate.getForObject(REST_URL_PREX + "dept/list1/" ,
                List.class);

    }


    @RequestMapping("/comsumer/dept/list")
    public List list() {
        System.out.println("comsumer list");
        return deptSercice.findAll();
    }

}
