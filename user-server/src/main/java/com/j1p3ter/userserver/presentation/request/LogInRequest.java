package com.j1p3ter.userserver.presentation.request;

import com.j1p3ter.userserver.domain.model.User;
import com.j1p3ter.userserver.domain.model.UserRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class LogInRequest {

    @NotEmpty
    @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "4자 이상, 10자 이하의 소문자 알파벳과 숫자만 가능합니다.")
    private String loginId;

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,15}$", message = "8자 이상, 15자 이하의 대소문자, 숫자, 특수문자만 가능합니다.")
    private String password;

}