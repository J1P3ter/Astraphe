package com.j1p3ter.gateway.infrastructure.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum InvalidUrlExceptionCase {

    INVALID_URL(HttpStatus.NOT_FOUND, "잘못된 url입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

}