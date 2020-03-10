package com.microservice.gateway.config;

import com.microservice.gateway.filter.ElapsedFilter;
import com.microservice.gateway.filter.TokenFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        // @formatter:off
//        return builder.routes()
//                .route(r -> r.path("/fluent/customer/**")
//                        .filters(f -> f.stripPrefix(2)
//                                .filter(new ElapsedFilter())
//                                .addResponseHeader("X-Response-Default-Foo", "Default-Bar"))
//                        .uri("lb://microservice-gateway")
//                        .order(0)
//                        .id("fluent_customer_service")
//                )
//                .build();
//    }

    @Bean
    public TokenFilter tokenFilter(){
        return new TokenFilter();
    }
}