package com.j1p3ter.gateway.infrastructure.jwt;

import com.j1p3ter.gateway.domain.model.UserRole;
import com.j1p3ter.gateway.infrastructure.config.InvalidTokenExceptionCase;
import com.j1p3ter.gateway.infrastructure.config.InvalidUrlExceptionCase;
import com.j1p3ter.gateway.infrastructure.exception.InvalidTokenException;
import com.j1p3ter.gateway.infrastructure.exception.InvalidUrlException;
import com.j1p3ter.gateway.infrastructure.infrastructure.AuthRule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private final QueueJwtUtil queueJwtUtil;

    private List<String> availableUrls;

    private List<String> nonfilteredUrls;

    private List<AuthRule> customerRules;
    private List<AuthRule> sellerRules;

    // Authorizing Manager: Optional
    private List<AuthRule> managerRules;

    @PostConstruct
    public void init() {
        availableUrls = Arrays.asList(
                "/api/auth/logIn","/api/auth/signUp",
                "/webjars", "/swagger-ui.html",
                "/api/auth/v3/api-docs", "/api/service/v3/api-docs",
                "/api/users", "/api/users/**",
                "/api/companies", "/api/companies/**",
                "/api/products", "/api/products/**",
                "/api/orders", "/api/orders/**",
                "/api/payments", "/api/payments/**",
                "/api/waitingQueue/**", "/api/waitingRoom/**", "/api/schedulingQueue/**"
                );

        nonfilteredUrls = Arrays.asList("/api/auth/logIn","/api/auth/signUp", "/webjars", "/swagger-ui.html",
                "/api/auth/v3/api-docs", "/api/service/v3/api-docs");

        customerRules = new ArrayList<>();
        customerRules.add(new AuthRule("/api/users", Set.of(HttpMethod.GET)));
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
        sellerRules.add(new AuthRule("/api/users", Set.of(HttpMethod.GET)));
        sellerRules.add(new AuthRule("/api/waitingQueue/{productId}/registerUser", Set.of(HttpMethod.POST)));
        sellerRules.add(new AuthRule("/api/waitingQueue/{productId}", Set.of(HttpMethod.GET)));
        sellerRules.add(new AuthRule("/api/waitingRoom/{productId}", Set.of(HttpMethod.GET)));
    }

    private boolean isAvailableUrl(String path) {
        for (String availableUrl : availableUrls) {
            if (path.matches(availableUrl.replace("**", ".*"))) {
                return true;
            }
        }
        return false;
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
        HttpMethod method = exchange.getRequest().getMethod();

        // 클라이언트 요청에서 Authorization 헤더(JWT 토큰) 추출
        String jwtToken = extractToken(exchange.getRequest().getHeaders().getFirst("Authorization"));

        if (!isAvailableUrl(path)) {
            throw new InvalidUrlException(InvalidUrlExceptionCase.INVALID_URL);
        }

        if (isNonfilteredUrl(path)) {
            return chain.filter(exchange);
        }

        // Product 인증 허가 확인
        if (path.matches("/api/products/\\d+") && method == HttpMethod.GET) {
            // 정규식을 사용하여 productId 추출
            Pattern pattern = Pattern.compile("/api/products/(\\d+)");
            Matcher matcher = pattern.matcher(path);

            Long productId = null;
            if (matcher.find()) {
                productId = Long.valueOf(matcher.group(1)); // 첫 번째 그룹에서 productId 추출
                log.info("Extracted Product ID: {}", productId); // 추출된 productId 로그 출력
            }

            // 토큰 유효성 검사
            String productToken = extractToken(exchange.getRequest().getHeaders()
                    .getFirst(queueJwtUtil.AUTHORIZATION_HEADER));

            // 토큰이 유효하지 않은 경우
            if (!queueJwtUtil.validateQueueToken(productToken) || productToken == null || productToken.isEmpty()) {
                log.info("Invalid Product Token, forwarding to register user in waiting queue...");

                // 요청 경로 변경: waitingQueue API로 리다이렉트
                URI newUri = URI.create("/api/waitingQueue/" + productId + "/registerUser");

                // 기존 요청을 수정해서 새로운 URI로 설정
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().uri(newUri).build();

                // 수정된 요청을 기반으로 새로운 ServerWebExchange 생성
                ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

                // 필터 체인 계속 실행 -> 새 경로로 전송
                return chain.filter(modifiedExchange)
                        .then(Mono.defer(() -> {
                            log.info("Request forwarded to new URI: {}", newUri);
                            return Mono.empty(); // 필터 체인 완료 후 비동기 응답 처리
                        }));
            }
            // 토큰이 유효한 경우 계속 필터 체인 실행
        }



        String accessToken = extractToken(exchange.getRequest().getHeaders().getFirst("Authorization"));

        // 인증 필요 API
        if (jwtUtil.validateToken(accessToken)) {
            UserRole userRole = jwtUtil.getRole(accessToken);

            // 잘못된 JWT
            if (userRole == null) {
                throw new InvalidTokenException(InvalidTokenExceptionCase.TOKEN_MISSING);
            }

            // 권한 없음
            if (!isAccessible(path, method, userRole.toString())) {
                throw new InvalidTokenException(InvalidTokenExceptionCase.TOKEN_UNAUTHORIZED);
            }

            // 헤더에 X-USER-ID 추가
            exchange.getRequest().mutate()
                    .header("X-USER-ID", jwtUtil.getUserId(accessToken))
                    .build();

            return chain.filter(exchange);

        } else {
            throw new InvalidTokenException(InvalidTokenExceptionCase.TOKEN_UNSUPPORTED);
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