package com.j1p3ter.gateway.presentation.exception;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.gateway.infrastructure.exception.GatewayException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class GatewayExceptionHandler {

    @ExceptionHandler(GatewayException.class)
    public ApiResponse<?> handleDnaApplicationException(GatewayException e) {
        return ApiResponse
                .error(HttpStatus.BAD_GATEWAY.value(), e.getMessage());
    }
}
