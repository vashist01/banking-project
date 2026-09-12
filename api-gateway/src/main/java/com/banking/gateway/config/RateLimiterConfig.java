package com.banking.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> {
            // Use IP address as the rate limiting key
            String ip = exchange.getRequest().getRemoteAddress()
                .getAddress().getHostAddress();
            String userId = exchange.getRequest().getHeaders().getFirst("X-userId");
            String userClientId = exchange.getRequest().getHeaders().getFirst("clientId");
            String userRateLimiting = userId+userClientId; // this approach is token bucket algorithem
            return Mono.just(userRateLimiting != null ? userRateLimiting : ip);
        };
    } 
}