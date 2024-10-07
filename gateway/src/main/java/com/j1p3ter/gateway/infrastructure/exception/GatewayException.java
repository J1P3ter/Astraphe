package com.j1p3ter.gateway.infrastructure.exception;

import com.j1p3ter.gateway.infrastructure.config.GatewayExceptionCase;
import lombok.Getter;

@Getter
public class GatewayException extends RuntimeException{

    private final GatewayExceptionCase exceptionCase;

    public GatewayException(GatewayExceptionCase exceptionCase) {
        super(exceptionCase.getMessage());
        this.exceptionCase = exceptionCase;
    }

}