package com.microservice.gateway;

import com.microservice.gateway.resolver.HostAddrKeyResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

/**
 * @SpringBootApplication springboot启动类注解
 * @EnableEurekaClient 启用eureka客服端
 */
@SpringBootApplication
@EnableEurekaClient
public class MyGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyGatewayApplication.class, args);
    }


    @Bean(name = HostAddrKeyResolver.BEAN_NAME)
    public HostAddrKeyResolver hostAddrKeyResolver() {
        return new HostAddrKeyResolver();
    }
}