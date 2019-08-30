package com.ldw.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class DeptProvider8002_App {
    public static void main(String[] args) {
        SpringApplication.run(DeptProvider8002_App.class, args);
    }
}
