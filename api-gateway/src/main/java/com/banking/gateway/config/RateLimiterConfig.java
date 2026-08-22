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

//    // Optional: Add additional key resolvers for different scenarios
//    @Bean
//    public KeyResolver userKeyResolver() {
//        return exchange -> {
//            // Use user ID from header or authentication
//            String userId = exchange.getRequest().getHeaders()
//                .getFirst("X-User-Id");
//            if (userId == null) {
//                userId = "anonymous";
//            }
//            return Mono.just(userId);
//        };
//    }
//
//    @Bean
//    public KeyResolver apiKeyResolver() {
//        return exchange -> {
//            // Use API key from header
//            String apiKey = exchange.getRequest().getHeaders()
//                .getFirst("X-API-Key");
//            if (apiKey == null) {
//                apiKey = "default";
//            }
//            return Mono.just(apiKey);
//        };
//    }
}