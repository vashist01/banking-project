package com.banking.account.config;

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