package com.ldw.microservice.docker.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DeptController {
    @RequestMapping("get")
    public void get() {
        System.out.println("get");
    }
}
