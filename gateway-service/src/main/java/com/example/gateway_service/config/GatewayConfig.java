package com.example.gateway_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

        @Value("${backend.base-url}")
        private String backendBaseUrl;

        @Bean
        public RouteLocator customRoutes(RouteLocatorBuilder builder) {
                return builder.routes()
                                // Route for 3-Player Chess
                                .route("three-player", r -> r
                                                .path("/three-player/**")
                                                .uri(backendBaseUrl))

                                // Route for 2-Player Chess
                                .route("two-player", r -> r
                                                .path("/two-player/**")
                                                .uri("http://twoplayer-service:8082"))
                                .build();
        }
}