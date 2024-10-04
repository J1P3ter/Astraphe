package com.j1p3ter.userserver.presentation.request;

import com.j1p3ter.userserver.domain.model.User;
import com.j1p3ter.userserver.domain.model.UserRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignUpRequestDto {

    @NotEmpty
    @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "4자 이상, 10자 이하의 소문자 알파벳과 숫자만 가능합니다.")
    private String loginId;

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,15}$", message = "8자 이상, 15자 이하의 대소문자, 숫자, 특수문자만 가능합니다.")
    private String password;

    @NotEmpty
    private String username;

    @NotEmpty
    private String nickname;

    @NotEmpty
    private String phoneNumber;

    @NotEmpty
    private String userRole;

    public User toEntity(String password) {
        return User.builder()
                .loginId(this.loginId)
                .password(password)
                .username(this.username)
                .nickname(this.nickname)
                .phoneNumber(this.phoneNumber)
                .userRole(UserRole.fromString(userRole))
                .build();
    }

}
