package com.j1p3ter.userserver.application.dto;

import com.j1p3ter.userserver.domain.model.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class UserUpdateResponseDto {

    private Long userId;
    private String loginId;
    private String password;
    private String username;
    private String nickname;
    private String phoneNum;
    private String shippingAddress;
    private String userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * password는 제외
     */
    public static UserUpdateResponseDto fromEntity(User user) {
        return UserUpdateResponseDto.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phoneNum(user.getPhoneNumber())
                .shippingAddress(user.getShippingAddress())
                .userRole(user.getUserRole().toString())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
