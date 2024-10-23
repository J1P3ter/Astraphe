package com.j1p3ter.productserver.presentation;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.productserver.application.SubscribeProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscribeProducts")
@RequiredArgsConstructor
@Slf4j(topic = "SubscribeProduct Controller")
public class SubscribeProductController {
    private final SubscribeProductService subscribeProductService;

    @Operation(summary = "Create SubscribeProduct")
    @PostMapping
    public ApiResponse<?> createSubscribeProduct(
            @RequestHeader(name = "X-USER-ID") Long userId,
            @RequestParam Long productId
    ){
        return ApiResponse.success(subscribeProductService.createSubscribeProduct(userId, productId));
    }

    @Operation(summary = "Get SubscribeProduct")
    @GetMapping("{subscribeProductId}")
    public ApiResponse<?> getSubscribeProduct(
            @RequestHeader(name = "X-USER-ID") Long userId,@PathVariable Long subscribeProductId
    ){
        return ApiResponse.success(subscribeProductService.getSubscribeProduct(subscribeProductId));
    }
    @Operation(summary = "Delete SubscribeProduct")
    @DeleteMapping("subscribeProductId")
    public ApiResponse<?> deleteSubscribeProduct(
            @RequestHeader(name = "X-USER-ID") Long userId,@PathVariable Long subscribeProductId
    ){
        return ApiResponse.success(subscribeProductService.deleteSubscribeProduct(userId, subscribeProductId));
    }
}
