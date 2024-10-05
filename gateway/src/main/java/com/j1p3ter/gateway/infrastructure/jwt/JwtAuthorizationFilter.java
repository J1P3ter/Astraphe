package com.j1p3ter.gateway.infrastructure.jwt;

import com.j1p3ter.gateway.domain.model.UserRole;
import com.j1p3ter.gateway.infrastructure.config.GatewayExceptionCase;
import com.j1p3ter.gateway.infrastructure.exception.GatewayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        HttpMethod method = exchange.getRequest().getMethod();
        String[] pathSegments = exchange.getRequest().getURI().getPath().split("/");
        String roleInPath = pathSegments[2];

        // 인증 필요없는 api
        if(roleInPath.equals("public")){
            return Mono.empty();
        } else {

            String token = extractToken(exchange.getRequest().getHeaders().getFirst("Authorization"));

            // token 없는 경우
            if (token == null) {
                throw new GatewayException(GatewayExceptionCase.TOKEN_MISSING);
            }

            // 인증 필요 API
            if (jwtUtil.validateToken(token)) {
                UserRole role = jwtUtil.getRole(token);

                // 잘못된 jwt
                if (role == null) {
                    throw new GatewayException(GatewayExceptionCase.TOKEN_MISSING);
                }

                // 권한 없음
                if (!isAuthorized(roleInPath, role)) {
                    throw new GatewayException(GatewayExceptionCase.TOKEN_UNAUTHORIZED);
                }

                // 헤더에 username 추가
                exchange.getRequest().mutate()
                        .header("username", jwtUtil.getUsername(token))
                        .build();

                return Mono.empty();
            } else {
                throw new GatewayException(GatewayExceptionCase.TOKEN_UNSUPPORTED);
            }
        }
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

    private boolean isAuthorized(String requestPath, UserRole role) {
        switch (role) {
            case ADMIN -> {
                return true;
            }
            case MANAGER -> {
                return !requestPath.equals("master");
            }
            case SELLER -> {
                return !requestPath.equals("master") && !requestPath.equals("hub-manager");
            }
            case CUSTOMER -> {
                return !requestPath.equals("master") && !requestPath.equals("hub-manager") && !requestPath.equals("delivery-agent");
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}