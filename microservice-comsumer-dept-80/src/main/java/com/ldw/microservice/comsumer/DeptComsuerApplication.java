package com.ldw.microservice.comsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;


@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.ldw.microservice.service")
public class DeptComsuerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeptComsuerApplication.class, args);
	}
}
