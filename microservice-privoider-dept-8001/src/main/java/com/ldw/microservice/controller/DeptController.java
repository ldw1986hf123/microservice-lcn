package com.ldw.microservice.controller;

import com.ldw.microservice.entity.Dept;
import com.ldw.microservice.service.DeptSercice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeptController {

	private @Autowired
	DeptSercice deptSercice;
	private @Autowired
	DiscoveryClient discoveryClient;

	@RequestMapping(value = "dept/get/{id}", method = RequestMethod.GET)
	public Dept get(@PathVariable("id") Long id) {
		System.out.print("get");
		Dept dept = deptSercice.findById(id);
		return dept;
	}

	@RequestMapping(value = "dept/add")
	public boolean add(@RequestBody Dept dept) {
		return deptSercice.addDpet(dept);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "dept/list", method = RequestMethod.GET)
	public List list() {
		return deptSercice.findAll();
	}

	@SuppressWarnings({"rawtypes"})
	@RequestMapping("/dept/discovery")
	public void discoveryService() {
		List list = discoveryClient.getServices();
		System.out.println("service list " + list);

		List<ServiceInstance> listServiceInstance = discoveryClient
				.getInstances("MICROSERVICE-DEPT");
		for (ServiceInstance serviceInstance : listServiceInstance) {
			System.out.println("getServiceId:" + serviceInstance.getServiceId()
					+ "\t getHost: " + serviceInstance.getHost()
					+ "\t  getPort:" + serviceInstance.getPort());
		}
	}

}
