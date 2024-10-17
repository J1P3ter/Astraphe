package com.j1p3ter.userserver.presentation.controller;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.userserver.application.service.UserService;
import com.j1p3ter.userserver.presentation.request.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("")
    public ApiResponse<?> getUsersInfo(@RequestHeader(name = "X-USER-ID") Long xUserId,
                                       @PageableDefault(page = 0, size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // 한 번에 10, 20개 단위로만 조회 가능
        if (!List.of(10, 20).contains(pageable.getPageSize())) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "한 번에 10, 20개 단위로만 조회 가능합니다.");
        }
        try {
            return ApiResponse.success(userService.getUsersInfo(pageable));
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