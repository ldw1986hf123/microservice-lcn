package com.ldw.microservice.docker.controller;


import com.ldw.microservice.entity.Dept;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeptController {
    @RequestMapping("get")
    public String get() {
        System.out.println("get");
        return "get";
    }


    @RequestMapping("getDept")
    public Dept get(Dept dept) {
        System.out.println("dept "+dept);
        return dept;
    }
}
