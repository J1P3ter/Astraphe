package com.j1p3ter.userserver.application.dto;

import com.j1p3ter.userserver.domain.model.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class UserDeleteResponseDto {
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * password는 제외
     */
    public static UserDeleteResponseDto fromEntity(User user) {
        return UserDeleteResponseDto.builder()
                .userId(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

}
