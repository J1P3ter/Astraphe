package com.j1p3ter.gateway.infrastructure.exception;

import com.j1p3ter.gateway.infrastructure.config.InvalidTokenExceptionCase;
import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException{

    private final InvalidTokenExceptionCase exceptionCase;

    public InvalidTokenException(InvalidTokenExceptionCase exceptionCase) {
        super(exceptionCase.getMessage());
        this.exceptionCase = exceptionCase;
    }

}