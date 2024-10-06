package com.j1p3ter.gateway.infrastructure.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GatewayExceptionCase {

    TOKEN_MISSING(HttpStatus.NOT_FOUND, "토큰이 없습니다."),
    TOKEN_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    TOKEN_UNSUPPORTED(HttpStatus.BAD_REQUEST, "잘못된 토큰입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

}