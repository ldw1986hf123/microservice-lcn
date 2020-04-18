package com.ldw.microservice.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("gateway")
public class TestController {

    @RequestMapping("gatwatTest")
    public void test() {
        System.out.println("gate twat");
    }
}
