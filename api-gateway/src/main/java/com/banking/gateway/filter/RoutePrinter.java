package com.banking.gateway.filter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.stereotype.Component;

@Component
public class RoutePrinter implements CommandLineRunner {

    private final RouteLocator routeLocator;

    public RoutePrinter(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @Override
    public void run(String... args) {
        routeLocator.getRoutes().subscribe(route ->
                System.out.println("Loaded Route : " + route.getId()
                        + " -> " + route.getUri()));
    }
}