package com.ldw.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class DeptProviderConfig_App_8001 {
	public static void main(String[] args) {
		SpringApplication.run(DeptProviderConfig_App_8001.class, args);
	}
}
