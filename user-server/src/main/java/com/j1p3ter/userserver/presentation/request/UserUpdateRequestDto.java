package com.j1p3ter.userserver.presentation.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateRequestDto {

    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,15}$", message = "8자 이상, 15자 이하의 대소문자, 숫자, 특수문자만 가능합니다.")
    private String password;
    private String nickname;
    private String phoneNumber;
    private Long slackId;

}