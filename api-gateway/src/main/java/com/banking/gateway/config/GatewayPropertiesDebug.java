package com.banking.gateway.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayPropertiesDebug {

    @Bean
    CommandLineRunner gatewayPropertiesRunner(GatewayProperties gatewayProperties) {
        return args -> {
            System.out.println("Number of routes = " + gatewayProperties.getRoutes().size());

            gatewayProperties.getRoutes().forEach(route -> {
                System.out.println("Route ID  : " + route.getId());
                System.out.println("URI       : " + route.getUri());
                System.out.println("Predicates: " + route.getPredicates());
                System.out.println("Filters   : " + route.getFilters());
            });
        };
    }
}