package com.j1p3ter.userserver.presentation.controller;

import com.j1p3ter.userserver.application.service.UserService;
import com.j1p3ter.userserver.presentation.request.UserCreateRequest;
import com.j1p3ter.userserver.presentation.response.CommonApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signUp")
    public CommonApiResponse signUp(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        return userService.createUser(userCreateRequest);
    }
}
