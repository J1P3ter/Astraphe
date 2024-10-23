package com.j1p3ter.gateway.infrastructure.jwt;

import com.j1p3ter.gateway.domain.model.UserRole;
import com.j1p3ter.gateway.infrastructure.config.InvalidQueueTokenExceptionCase;
import com.j1p3ter.gateway.infrastructure.config.InvalidTokenExceptionCase;
import com.j1p3ter.gateway.infrastructure.exception.InvalidQueueTokenException;
import com.j1p3ter.gateway.infrastructure.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;

@Slf4j
@Component
public class QueueJwtUtil {
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "queueToProduct";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "queueTokenKey";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret.queueKey}") // Base64 Encode 한 SecretKey
    private String queueSecretKey;
    private Key queueJwtSecretKey;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(queueSecretKey);
        queueJwtSecretKey = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 유효성 검사
    // 토큰 검증
    public boolean validateQueueToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(queueJwtSecretKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid Queue JWT signature, 유효하지 않는 Queue JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired Queue JWT token, 만료된 Queue JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported Queue JWT token, 지원되지 않는 Queue JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("Queue JWT claims is empty, 잘못된 Queue JWT 토큰 입니다.");
        }catch (SignatureException e){
            log.error("Queue JWT validity cannot be asserted and should not be trusted.");
        }
        return false;
    }


    // productId 추출
    public Long getProductId(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(queueJwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return (Long) claims.get(AUTHORIZATION_KEY);
    }
}