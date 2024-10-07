package com.j1p3ter.userserver.application.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LogInResponseDto {
    private String accessToken;

    public static LogInResponseDto fromString(String accessToken) {
        return LogInResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
