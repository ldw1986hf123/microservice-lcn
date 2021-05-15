package com.ldw.microservice.docker;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeptProviderDocker_8004{
    public static void main(String[] args) {
        SpringApplication.run(DeptProviderDocker_8004.class, args);
    }
}
