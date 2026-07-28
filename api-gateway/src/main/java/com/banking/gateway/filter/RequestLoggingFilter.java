package com.banking.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestLoggingFilter implements GlobalFilter, Ordered {
    public RequestLoggingFilter() {
        log.info("RequestLoggingFilter initialized");
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Incoming Request -> Method: {}, URI: {}, Client IP: {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI(),
                exchange.getRequest().getRemoteAddress());
        return chain.filter(exchange).doOnSuccess(aVoid ->
                log.info("Response Status: {}",
                        exchange.getResponse().getStatusCode()));
    }

    @Override
    public int getOrder() {
        return 0;   // Execute before other filters
    }
}
