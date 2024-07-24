package com.ldw.microservice.hystrix.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;


@EnableHystrixDashboard
@EnableHystrix
public class DeptHystrixDashboardApplication {
	public static void main(String[] args) {
		SpringApplication.run(DeptHystrixDashboardApplication.class, args);
	}

}
