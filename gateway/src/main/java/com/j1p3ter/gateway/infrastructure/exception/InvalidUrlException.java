package com.j1p3ter.gateway.infrastructure.exception;

import com.j1p3ter.gateway.infrastructure.config.InvalidUrlExceptionCase;
import lombok.Getter;

@Getter
public class InvalidUrlException extends RuntimeException{

    private final InvalidUrlExceptionCase exceptionCase;

    public InvalidUrlException(InvalidUrlExceptionCase exceptionCase) {
        super(exceptionCase.getMessage());
        this.exceptionCase = exceptionCase;
    }

}