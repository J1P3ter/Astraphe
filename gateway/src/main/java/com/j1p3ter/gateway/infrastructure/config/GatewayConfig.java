package com.j1p3ter.gateway.infrastructure.config;

import com.j1p3ter.gateway.infrastructure.jwt.JwtAuthorizationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    /**
     * TODO 코드로 routing을 설정합니다
     * application.yml 과 동일한 설정으로 작성해주세요!
     *
     * @param builder
     * @param jwtAuthorizationFilter
     * @return
     */
    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder, JwtAuthorizationFilter jwtAuthorizationFilter) {
        return builder.routes()
                .route("user-auth", route -> route
                        .path("/api/auth/**")
                        .uri("lb://user-server")
                )
                .route("user-cruds", route -> route
                        .path("/api/users/**")
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
                        .path("/api/products/**")
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
                        .path("/api/orders/**")
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
                        .path("/api/queues/**")
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