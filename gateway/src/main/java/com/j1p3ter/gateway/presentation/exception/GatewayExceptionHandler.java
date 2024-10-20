package com.j1p3ter.gateway.presentation.exception;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.gateway.infrastructure.exception.InvalidTokenException;
import com.j1p3ter.gateway.infrastructure.exception.InvalidUrlException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class GatewayExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ApiResponse<?> handleInvalidTokenException(InvalidTokenException e) {
        return ApiResponse
                .error(HttpStatus.BAD_GATEWAY.value(), e.getMessage());
    }

    @ExceptionHandler(InvalidUrlException.class)
    public ApiResponse<?> handleInvalidUrlException(InvalidUrlException e) {
        return ApiResponse
                .error(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

}
