package com.j1p3ter.userserver.presentation.controller;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.userserver.application.service.UserService;
import com.j1p3ter.userserver.presentation.request.LogInRequestDto;
import com.j1p3ter.userserver.presentation.request.SignUpRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signUp")
    public ApiResponse<?> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        try {
            return ApiResponse.success(userService.createUser(signUpRequestDto));
        } catch (ApiException e) {
            return ApiResponse.error(e.getHttpStatus().value(), e.getMessage());
        }
    }

    @PostMapping("/logIn")
    public ApiResponse<?> logIn(@RequestBody LogInRequestDto logInRequestDto,
                                HttpServletResponse response) {
        try {
            return ApiResponse.success(userService.logIn(logInRequestDto, response));
        } catch (ApiException e) {
            return ApiResponse.error(e.getHttpStatus().value(), e.getMessage());
        }
    }

}
