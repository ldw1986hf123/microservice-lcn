package com.ldw.microservice.docker.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeptController {
    @RequestMapping("get")
    public String get() {
        System.out.println("get");
        return "get";
    }
}
