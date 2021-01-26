package com.ldw.microservice.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ldw.microservice.entity.Dept;

@FeignClient(name = "MICROSERVICE-DEPT" )
public interface DeptFeignService {

    @RequestMapping("/dept/add")
    public boolean addDept(Dept dept);

    @RequestMapping("/dept/get/{id}")
    public Dept get(@PathVariable("id") Long id);

    @SuppressWarnings("rawtypes")
    @RequestMapping("/dept/list1")
    public List list();
}
