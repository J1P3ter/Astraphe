package com.j1p3ter.common.exception;

import com.j1p3ter.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> globalExceptionHandler(ApiException e) {
        log.error(e.getLogMessage());
        return ResponseEntity
                .status(e.getHttpStatus().value())
                .body(ApiResponse.error(e.getHttpStatus().value(), e.getMessage()));
    }
}
