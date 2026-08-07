package com.banking.account.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupChecker {

    private final io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry registry;

    @PostConstruct
    public void init() {

        System.out.println("Registered Circuit Breakers:");

        registry.getAllCircuitBreakers()
                .forEach(cb -> System.out.println(cb.getName()));
    }
}