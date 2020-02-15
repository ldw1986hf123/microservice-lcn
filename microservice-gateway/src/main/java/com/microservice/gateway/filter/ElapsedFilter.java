//package com.microservice.gateway.filter;
//
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.core.Ordered;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//
//public class ElapsedFilter implements GatewayFilter, Ordered {
//
//    private static final String ELAPSED_TIME_BEGIN = "elapsedTimeBegin";
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        exchange.getAttributes().put(ELAPSED_TIME_BEGIN, System.currentTimeMillis());
//        return chain.filter(exchange).then(
//                Mono.fromRunnable(() -> {
//                    Long startTime = exchange.getAttribute(ELAPSED_TIME_BEGIN);
//                    if (startTime != null) {
//                        System.out.println(exchange.getRequest().getURI().getRawPath() + ": " + (System.currentTimeMillis() - startTime) + "ms");
//                    }
//                })
//        );
//    }
//
//
//    /**
//     * 方法是来给过滤器设定优先级别的，值越大则优先级越低
//     *
//     * @return
//     */
//    @Override
//    public int getOrder() {
//        return Ordered.LOWEST_PRECEDENCE;
//    }
//}
