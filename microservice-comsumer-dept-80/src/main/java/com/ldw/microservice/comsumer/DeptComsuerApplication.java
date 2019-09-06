package com.ldw.microservice.comsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

//import MyRule.LdwSelfRule;

@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name="MICROSERVICE-DEPT")
public class DeptComsuerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeptComsuerApplication.class, args);
	}
}
