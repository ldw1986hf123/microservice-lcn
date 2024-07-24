package com.ldw.microservice.comsumer.dept;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigBean {

	@Bean
	@LoadBalanced
	// springcloud ribbon基于netflix ribbon实现的一套客户端负载均衡的工具
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

//	@Bean    //springcloud的负载均衡核心组件，IRule
//	public IRule MyselfRule() {
////		return new RandomRule();
//		return new RetryRule();
//	}

}
