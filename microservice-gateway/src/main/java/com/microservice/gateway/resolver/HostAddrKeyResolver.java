package com.microservice.gateway.resolver;


import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class HostAddrKeyResolver implements KeyResolver {
    public static final String BEAN_NAME = "hostAddrKeyResolver";
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }
//    @Bean(name = HostAddrKeyResolver.BEAN_NAME)
//    public HostAddrKeyResolver hostAddrKeyResolver() {
//        return new HostAddrKeyResolver();
//    }



}
