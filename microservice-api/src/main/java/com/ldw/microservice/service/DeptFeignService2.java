package com.ldw.microservice.service;

import com.ldw.microservice.entity.Dept;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "MICROSERVICE-DEPT2")
//@FeignClient(value = "MICROSERVICE-DEPT1", fallbackFactory = DeptFeignClient.class)
public interface DeptFeignService2 {

	@RequestMapping("/dept/add")
	boolean addDept(Dept dept);

	@RequestMapping("/dept/get/{id}")
	Dept get(@PathVariable("id") Long id);

	@RequestMapping("/dept/list")
	List list();
}
