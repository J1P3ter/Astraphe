package com.j1p3ter.gateway.infrastructure.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum InvalidQueueTokenExceptionCase {

    TOKEN_MISSING(HttpStatus.NOT_FOUND, "Queue 토큰이 없습니다."),
    TOKEN_UNSUPPORTED(HttpStatus.BAD_REQUEST, "잘못된 Queue 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 Queue 토큰입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

}