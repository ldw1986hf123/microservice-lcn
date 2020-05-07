package com.microservice.gateway.resolver;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class UriKeyResolver implements KeyResolver {

    public static final String BEAN_NAME = "uriKeyResolver";
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest().getURI().getPath());
    }

//    @Bean(name = UriKeyResolver.BEAN_NAME)
//    public UriKeyResolver uriKeyResolver() {
//        return new UriKeyResolver();
//    }

}
