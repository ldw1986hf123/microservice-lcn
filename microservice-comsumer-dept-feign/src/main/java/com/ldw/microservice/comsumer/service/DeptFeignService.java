//package com.ldw.microservice.comsumer.service;
//
//import com.ldw.microservice.entity.Dept;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.List;
//
//@FeignClient(name = "MICROSERVICE-DEPT" )
//public interface DeptFeignService {
//
//    @RequestMapping("/dept/add")
//    public boolean addDept(Dept dept);
//
//    @RequestMapping("/dept/get/{id}")
//    public Dept get(@PathVariable("id") Long id);
//
//    @SuppressWarnings("rawtypes")
//    @RequestMapping("/dept/list")
//    public List list();
//}
