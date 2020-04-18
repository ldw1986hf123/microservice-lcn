package com.ldw.microservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("config")
public class TestController {


    @RequestMapping("test")
    public String getConfig() {
        System.out.println("test");
        return null;
    }

}
