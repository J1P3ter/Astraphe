package com.j1p3ter.gateway.infrastructure.jwt;

import com.j1p3ter.gateway.domain.model.UserRole;
import com.j1p3ter.gateway.infrastructure.config.GatewayExceptionCase;
import com.j1p3ter.gateway.infrastructure.exception.GatewayException;
import com.j1p3ter.gateway.infrastructure.infrastructure.AuthRule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private List<String> nonfilteredUrls;

    private List<AuthRule> customerRules;
    private List<AuthRule> sellerRules;

    // Authorizing Manager: Optional
    private List<AuthRule> managerRules;

    @PostConstruct
    public void init() {
        nonfilteredUrls = Arrays.asList("/api/auth/logIn","/api/auth/signUp", "/webjars", "/swagger-ui.html",
                "/api/auth/v3/api-docs", "/api/service/v3/api-docs");

        customerRules = new ArrayList<>();
        customerRules.add(new AuthRule("/api/companies/{companyId}", Set.of(HttpMethod.PUT, HttpMethod.DELETE)));
        customerRules.add(new AuthRule("/api/companies", Set.of(HttpMethod.POST)));
        customerRules.add(new AuthRule("/api/products/{productId}", Set.of(HttpMethod.PUT, HttpMethod.DELETE)));
        customerRules.add(new AuthRule("/api/products", Set.of(HttpMethod.POST)));
        customerRules.add(new AuthRule("/api/orders/{orderId}", Set.of(HttpMethod.DELETE)));
        customerRules.add(new AuthRule("/api/payments/{paymentId}", Set.of(HttpMethod.PUT, HttpMethod.DELETE)));
        customerRules.add(new AuthRule("/api/payments", Set.of(HttpMethod.POST)));
        customerRules.add(new AuthRule("/api/waitingQueue/{productId}/registerUser", Set.of(HttpMethod.POST)));
        customerRules.add(new AuthRule("/api/waitingQueue/{productId}", Set.of(HttpMethod.GET)));
        customerRules.add(new AuthRule("/api/waitingRoom/{productId}", Set.of(HttpMethod.GET)));

        sellerRules = new ArrayList<>();
        sellerRules.add(new AuthRule("/api/carts/**", Set.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)));
        sellerRules.add(new AuthRule("/api/reviews/{reviewId}", Set.of(HttpMethod.PUT, HttpMethod.DELETE)));
        sellerRules.add(new AuthRule("/api/reviews/report/{reviewId}", Set.of(HttpMethod.PUT)));
        sellerRules.add(new AuthRule("/api/reviews", Set.of(HttpMethod.POST)));
        sellerRules.add(new AuthRule("/api/waitingQueue/**", Set.of(HttpMethod.GET, HttpMethod.POST)));
        sellerRules.add(new AuthRule("/api/waitingRoom/{productId}", Set.of(HttpMethod.GET)));
    }

    private boolean isNonfilteredUrl(String path) {
        for (String nonfilteredUrl: nonfilteredUrls) {
            if (nonfilteredUrl.equals(path)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAccessible(String endPoint, HttpMethod method, String role) {
        if (role.equals("CUSTOMER")) {
            for (AuthRule customerRule: customerRules) {
                String endPointPattern = customerRule.getEndpointPattern();

                if (endPointPattern.endsWith("/**")) {
                    String prefix = endPointPattern.replace("/**", "");
                    if (endPoint.startsWith(prefix) && customerRule.getDeniedMethods().contains(method)) {
                        return false;
                    }
                } else {
                    if (endPoint.equals(endPointPattern) && customerRule.getDeniedMethods().contains(method)) {
                        return false;
                    }
                }
            }
            return true;
        }
        else if (role.equals("SELLER")) {
            for (AuthRule sellerRule: sellerRules) {
                String endPointPattern = sellerRule.getEndpointPattern();

                if (endPointPattern.endsWith("/**")) {
                    String prefix = endPointPattern.replace("/**", "");
                    if (endPoint.startsWith(prefix) && sellerRule.getDeniedMethods().contains(method)) {
                        return false;
                    }
                } else {
                    if (endPoint.equals(endPointPattern) && sellerRule.getDeniedMethods().contains(method)) {
                        return false;
                    }
                }
            }
            return true;
        }
        // Optional
        else if (role.equals("MANAGER")) {
            for (AuthRule managerRule: managerRules) {
                String endPointPattern = managerRule.getEndpointPattern();

                if (endPointPattern.endsWith("/**")) {
                    String prefix = endPointPattern.replace("/**", "");
                    if (endPoint.startsWith(prefix) && managerRule.getDeniedMethods().contains(method)) {
                        return false;
                    }
                } else {
                    if (endPoint.equals(endPointPattern) && managerRule.getDeniedMethods().contains(method)) {
                        return false;
                    }
                }
            }
            return true;
        }
        else if (role.equals("ADMIN")){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isNonfilteredUrl(path)) {
            return chain.filter(exchange);
        }

        HttpMethod method = exchange.getRequest().getMethod();

        String accessToken = extractToken(exchange.getRequest().getHeaders().getFirst("Authorization"));

        // 인증 필요 API
        if (jwtUtil.validateToken(accessToken)) {
            UserRole userRole = jwtUtil.getRole(accessToken);

            // 잘못된 JWT
            if (userRole == null) {
                throw new GatewayException(GatewayExceptionCase.TOKEN_MISSING);
            }

            // 권한 없음
            if (!isAccessible(path, method, userRole.toString())) {
                throw new GatewayException(GatewayExceptionCase.TOKEN_UNAUTHORIZED);
            }

            // 헤더에 X-USER-ID 추가
            exchange.getRequest().mutate()
                    .header("X-USER-ID", jwtUtil.getUserId(accessToken))
                    .build();

            return chain.filter(exchange);

        } else {
            throw new GatewayException(GatewayExceptionCase.TOKEN_UNSUPPORTED);
        }
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}