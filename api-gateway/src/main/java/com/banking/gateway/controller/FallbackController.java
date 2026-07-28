package com.banking.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/auth")
    public Mono<Map<String, Object>> authFallback() {
        return Mono.just(fallbackResponse("Auth Service is currently unavailable"));
    }

    @GetMapping("/customer")
    public Mono<Map<String, Object>> customerFallback() {
        return Mono.just(fallbackResponse("Customer Service is currently unavailable"));
    }

    @GetMapping("/account")
    public Mono<Map<String, Object>> accountFallback() {
        return Mono.just(fallbackResponse("Account Service is currently unavailable"));
    }

    @GetMapping("/transaction")
    public Mono<Map<String, Object>> transactionFallback() {
        return Mono.just(fallbackResponse("Transaction Service is currently unavailable"));
    }

    private Map<String, Object> fallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Service Unavailable");
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}