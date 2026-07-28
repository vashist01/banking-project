package com.banking.gateway.config;

import com.banking.gateway.filter.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class GatewayConfig {
        private final RateLimiterConfig rateLimiterConfig;
    private final JwtTokenFilter authenticationFilter;
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("ACCOUNT-SERVICE", r -> r
                        .path("/api/v1/account/**")
                        .filters(f -> f
                                .filter(((exchange, chain) -> {
                                    System.out.println("Request: " + exchange.getRequest().getURI());
                                    return chain.filter(exchange);
                                }))
                                .requestRateLimiter(config -> {
                                    config.setKeyResolver(rateLimiterConfig.keyResolver());
                                    config.setRateLimiter(redisRateLimiter());
                                }))
                        .uri("http://localhost:8007"))
                .route("TRANSACTION-SERVICE", r -> r
                        .path("/api/v1/transaction/**")
                        .filters(f -> f
                                .filter(((exchange, chain) -> {
                                    System.out.println("Request: " + exchange.getRequest().getURI());
                                    return chain.filter(exchange);
                                }))
                                .requestRateLimiter(config -> {
                                    config.setKeyResolver(rateLimiterConfig.keyResolver());
                                    config.setRateLimiter(redisRateLimiter());
                                }))
                        .uri("http://localhost:8008"))
                .route("PAYMENT-SERVICE", r -> r
                        .path("/api/v1/payment/**")
                        .filters(f -> f
                                .filter(((exchange, chain) -> {
                                    System.out.println("Request: " + exchange.getRequest().getURI());
                                    return chain.filter(exchange);
                                }))
                                .requestRateLimiter(config -> {
                                    config.setKeyResolver(rateLimiterConfig.keyResolver());
                                    config.setRateLimiter(redisRateLimiter());
                                }))
                        .uri("http://localhost:8009")).
                route("CUSTOMER-SERVICE", r -> r
                        .path("/api/v1/customers/**")
                        .filters(f -> f
                                .filter(((exchange, chain) -> {
                                    System.out.println("Request: " + exchange.getRequest().getURI());
                                    return chain.filter(exchange);
                                }))
                                .requestRateLimiter(config -> {
                                    config.setKeyResolver(rateLimiterConfig.keyResolver());
                                    config.setRateLimiter(redisRateLimiter());
                                }))
                        .uri("http://localhost:8082"))
                .build();
    }


    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }
}