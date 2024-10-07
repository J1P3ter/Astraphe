package com.j1p3ter.userserver.presentation.controller;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.userserver.application.service.UserService;
import com.j1p3ter.userserver.presentation.request.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ApiResponse<?> getUserInfo(@RequestHeader(name = "X-USER-ID") Long xUserId,
                                      @PathVariable(name = "userId") Long userId) {
        try {
            return ApiResponse.success(userService.getUserInfo(xUserId, userId));
        } catch (ApiException e) {
            return ApiResponse.error(e.getHttpStatus().value(), e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    public ApiResponse<?> updateUserInfo(@RequestHeader(name = "X-USER-ID") Long xUserId,
                                         @PathVariable(name = "userId") Long userId,
                                         @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        try {
            return ApiResponse.success(userService.updateUserInfo(xUserId, userId, userUpdateRequestDto));
        } catch (ApiException e) {
            return ApiResponse.error(e.getHttpStatus().value(), e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<?> deleteUser(@RequestHeader(name = "X-USER-ID") Long xUserId,
                                     @PathVariable(name = "userId") Long userId) {
        try {
            return ApiResponse.success(userService.deleteUser(xUserId, userId));
        } catch (ApiException e) {
            return ApiResponse.error(e.getHttpStatus().value(), e.getMessage());
        }
    }

}