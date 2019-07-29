package com.ldw.microservice.comsumer;

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableFeignClients
@EnableDistributedTransaction
public class DeptComsuerFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeptComsuerFeignApplication.class, args);
	}
}
