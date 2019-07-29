package com.ldw.microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ldw.microservice.entity.Dept;
import com.ldw.microservice.service.DeptSercice;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class DeptController {

	private @Autowired DeptSercice deptSercice;
	private @Autowired DiscoveryClient discoveryClient;

	@RequestMapping(value = "dept/get/{id}", method = RequestMethod.GET)
	@HystrixCommand(fallbackMethod = "process_Hystrix")
	public Dept get(@PathVariable("id") Long id) {
		Dept dept = deptSercice.findById(id);
		if (null == dept) {
			throw new RuntimeException("该ID：" + id + "找不到对于的激烈");
		}
		return dept;
	}

	public Dept process_Hystrix(@PathVariable("id") Long id) {
		return new Dept().setDb_source("hystrix_dbsource").setDName(
				"没有对应的信息，所以返回null-hystrix");
	}

}
