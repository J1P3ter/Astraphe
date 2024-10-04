package com.j1p3ter.common.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse<T> {
    private int status;
    private T data;
    private String message;
    private LocalDateTime timeStamp;

    public static <T> ApiResponse<?> success(T data) {
        return ApiResponse.builder()
                .status(200)
                .data(data)
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<?> error(int status, String message) {
        return ApiResponse.builder()
                .status(status)
                .message(message)
                .timeStamp(LocalDateTime.now())
                .build();
    }
}