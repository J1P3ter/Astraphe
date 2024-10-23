package com.j1p3ter.productserver.application.client;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-server")
public interface UserClient {
    @GetMapping("/{userId}")
    public ApiResponse<?> getUserInfo(@RequestHeader(name = "X-USER-ID") Long xUserId,
                                      @PathVariable(name = "userId") Long userId);
}