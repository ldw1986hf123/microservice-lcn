package com.ldw.microservice.controller;

import com.ldw.microservice.entity.Dept;
import com.ldw.microservice.service.DeptFeignService;
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


    private @Autowired
    DeptFeignService deptFeignService;


    @RequestMapping(value = "dept/get/{id}", method = RequestMethod.GET)
    public Dept get(@PathVariable("id") Long id) {
        return deptSercice.findById(id);
    }

    @RequestMapping(value = "dept/add", method = RequestMethod.POST)
    public boolean add(@RequestBody Dept dept) {
        return deptSercice.addDpet(dept);
    }

    @RequestMapping(value = "dept/list", method = RequestMethod.GET)
    public List list() {
        return deptSercice.findAll();
    }


    @RequestMapping("/dept/discovery")
    public List discoveryService() {
        List list = discoveryClient.getServices();
        System.out.println("dgdfgdgg list " + list);

        List<ServiceInstance> listServiceInstance = discoveryClient
                .getInstances("MICROSERVICE-DEPT");
        for (ServiceInstance serviceInstance : listServiceInstance) {
            System.out.println("getServiceId:" + serviceInstance.getServiceId()
                    + "\t getHost: " + serviceInstance.getHost()
                    + "\t  getPort:" + serviceInstance.getPort());
        }
        return list;
    }

    @RequestMapping(value = "feign/list", method = RequestMethod.GET)
    public List feignList() {
        return deptFeignService.list();
    }


}
