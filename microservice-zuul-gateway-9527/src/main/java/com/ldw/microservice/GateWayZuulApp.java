package com.ldw.microservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy// 对zuul的支持
public class GateWayZuulApp {
	public static void main(String[] args) {
		SpringApplication.run(GateWayZuulApp.class, args);
	}
}
