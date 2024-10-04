package com.j1p3ter.userserver.presentation.controller;

import com.j1p3ter.userserver.application.service.UserService;
import com.j1p3ter.userserver.presentation.request.LogInRequestDto;
import com.j1p3ter.userserver.presentation.request.SignUpRequestDto;
import com.j1p3ter.userserver.presentation.response.CommonApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signUp")
    public CommonApiResponse signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        return userService.createUser(signUpRequestDto);
    }

    @PostMapping("/logIn")
    public CommonApiResponse logIn(@Valid @RequestBody LogInRequestDto logInRequestDto,
                                   HttpServletResponse response) {
        return userService.logIn(logInRequestDto, response);
    }

}
