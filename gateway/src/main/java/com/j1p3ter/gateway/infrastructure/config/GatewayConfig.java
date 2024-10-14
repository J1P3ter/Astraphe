package com.j1p3ter.gateway.infrastructure.config;

import com.j1p3ter.gateway.infrastructure.jwt.JwtAuthorizationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder, JwtAuthorizationFilter jwtAuthorizationFilter) {
        return builder.routes()
                .route("user-auth", route -> route
                        .path("/api/auth/**")
                        .uri("lb://user-server")
                )
                .route("user-cruds", route -> route
                        .path("/api/users/**", "/api/carts/**")
                        .filters(filter -> filter
                                .filter((exchange, chain) -> jwtAuthorizationFilter
                                        .filter(exchange, chain)
                                        .then(chain
                                                .filter(exchange)
                                        )
                                )
                        )
                        .uri("lb://user-server")
                )
                .route("product-server", route -> route
                        .path("/api/products/**", "/api/companies/**")
                        .filters(filter -> filter
                                .filter((exchange, chain) -> jwtAuthorizationFilter
                                        .filter(exchange, chain)
                                        .then(chain
                                                .filter(exchange)
                                        )
                                )
                        )
                        .uri("lb://product-server")
                )
                .route("order-server", route -> route
                        .path("/api/orders/**", "/api/payments/**", "/api/reviews/**")
                        .filters(filter -> filter
                                .filter((exchange, chain) -> jwtAuthorizationFilter
                                        .filter(exchange, chain)
                                        .then(chain
                                                .filter(exchange)
                                        )
                                )
                        )
                        .uri("lb://order-server")
                )
                .route("queue-server", route -> route
                        .path("/api/waitingQueue/**", "api/waitingRoom/**")
                        .filters(filter -> filter
                                .filter((exchange, chain) -> jwtAuthorizationFilter
                                        .filter(exchange, chain)
                                        .then(chain
                                                .filter(exchange)
                                        )
                                )
                        )
                        .uri("lb://queue-server")
                )
                .build();
    }
}