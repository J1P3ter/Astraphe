package com.j1p3ter.userserver.application.dto;

import com.j1p3ter.userserver.domain.model.User;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder // 빌더 패턴 협의
@Getter
public class LogInResponseDto {
    private Long userId;
    private String loginId;
    private String password;
    private String username;
    private String nickname;
    private String phoneNum;
    private String userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LogInResponseDto fromEntity(User user) {
        return LogInResponseDto.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phoneNum(user.getPhoneNumber())
                .userRole(user.getUserRole().toString())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static LogInResponseDto logInOf(String loginId, String password) {
        return LogInResponseDto.builder()
                .loginId(loginId)
                .password(password)
                .build();
    }
}
