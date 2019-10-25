//package com.ldw.microservice.comsumer.controller;
//
//import com.ldw.microservice.comsumer.service.DeptFeignService;
//import com.ldw.microservice.comsumer.service.DeptSercice;
//import com.ldw.microservice.entity.Dept;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//public class DeptFeignConsumerController {
//
//    // private final String REST_URL_PREX = "http://localhost:8001/";
//    // private final String REST_URL_PREX = "http://MICROSERVICE-DEPT/";
//
//
//    @Autowired
//    private DeptSercice deptSercice;
//    @Autowired
//    private DeptFeignService deptFeignService;
//
//    @RequestMapping(value = "/comsumer/dept/add", method = RequestMethod.POST)
//    public boolean addDept(Dept dept) {
//        System.out.println("dept :" + dept);
//        return deptSercice.addDpet(dept);
//    }
//
//    @RequestMapping(value = "/comsumer/dept/feignGetList", method = RequestMethod.POST)
//    public List feignGetList(Dept dept) {
//        List list = deptFeignService.list();
//        return list;
//    }
//
//
//
//
//
//    @RequestMapping(value = "/comsumer/dept/lcnTxTestAdd", method = RequestMethod.POST)
//    public void lcnTxTestAdd() {
//        Dept dept1 = new Dept();
//        dept1.setDeptNo(System.currentTimeMillis());
//        dept1.setDName("aa");
//        deptSercice.lcnTxTestAdd(dept1);
//    }
//
//
//    @RequestMapping(value = "/comsumer/dept/get/{id}", method = RequestMethod.POST)
//    public Dept get(@PathVariable("id") Long id) {
//        System.out.println("comsumer get");
//        Dept dept = deptSercice.findById(id);
//        return dept;
//    }
//
//    @SuppressWarnings("rawtypes")
//    @RequestMapping(value = "/comsumer/dept/list", method = RequestMethod.POST)
//    public List list() {
//        System.out.println("comsumer list");
//        return deptSercice.findAll();
//    }
//}
