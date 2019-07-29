package com.ldw.microservice.hystrix.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;


@SpringBootApplication
@EnableHystrixDashboard
public class DeptHystrixDashboardApplication {
	public static void main(String[] args) {
		SpringApplication.run(DeptHystrixDashboardApplication.class, args);
	}

}
