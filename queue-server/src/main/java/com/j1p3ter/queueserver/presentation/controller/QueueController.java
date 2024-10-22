package com.j1p3ter.queueserver.presentation.controller;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.queueserver.application.client.ProductClient;
import com.j1p3ter.queueserver.application.dto.AllowResponseDto;
import com.j1p3ter.queueserver.application.dto.ProductResponseDto;
import com.j1p3ter.queueserver.application.dto.RankResponseDto;
import com.j1p3ter.queueserver.application.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/waitingQueue")
@Slf4j(topic = "Queue Controller")
public class QueueController {
    private final QueueService queueService;
    private final ProductClient productClient;

    @Operation(summary = "사용자를 대기열에 등록 / rank를 리턴")
    @PostMapping("/{productId}/registerUser")
    public Mono<RankResponseDto> registerUser(@RequestHeader(name = "X-USER-ID") Long userId,
                                              @PathVariable Long productId){
        return queueService.registerWaitQueue(userId,productId)
                .map(rank -> new RankResponseDto(userId,rank));
    }

    // 대기열 queue에서 삭제 > 진입 queue에 추가
    @Operation(summary = "사용자 count 명을 접근 허용")
    @PostMapping("/{productId}/allow/{count}")
    public Mono<AllowResponseDto> allowUser(@PathVariable Long productId,
                                            @PathVariable Long count){
        return queueService.allowUser(productId,count)
                .map(allowed -> new AllowResponseDto(count,allowed));
    }

    @Operation(summary = "사용자 진입 가능 여부 boolean으로 리턴")
    @GetMapping("/{productId}/isAllowed")
    public Mono<Boolean> isAllowedUser(@RequestHeader(name = "X-USER-ID") Long userId,
                                 @PathVariable Long productId){
        return queueService.isAllowed(userId,productId);
    }

    // 사용자 접근 후 대기열에 사람 없을 경우 > 바로 product로 / 사람 있을 경우 대기열에 추가
    @Operation(summary = "Queue Request Handler")
    @PostMapping("/{productId}")
    public Mono<Object> handleQueueRequest(@RequestHeader(name = "X-USER-ID") Long userId,
                                           @PathVariable Long productId) {
        return queueService.getWaitingUsers(productId)
                .flatMap(waitingCount -> {
                    if (waitingCount == 0) {
                        // 대기열이 비어 있는 경우: product 정보 반환
                        return Mono.just(productClient.getProduct(productId));
                    } else {
                        // 대기열에 등록
                        return queueService.registerWaitQueue(userId, productId)
                                .map(rank -> new RankResponseDto(userId,rank));
                    }
                });
    }

    // 대기열 목록 조회
    @Operation(summary = "productId 별 대기열 목록 조회")
    @GetMapping("/{productId}/getQueueList")
    public Flux<RankResponseDto> getQueueList(@RequestHeader(name = "X-USER-ID") Long userId,
                                              @PathVariable Long productId){
        return queueService.getQueueList(productId);
    }

    @GetMapping("/{productId}/getProduct")
    public Mono<ProductResponseDto> getProduct(@RequestHeader(name = "X-USER-ID") Long userId,
                                                 @PathVariable Long productId){
        return productClient.forwardToProduct(productId);
    }

    @GetMapping("/{productId}/getToken")
    public Mono<String> forwardToProduct(@RequestHeader(name = "X-USER-ID") Long userId,
                                         @PathVariable Long productId) {
        return Mono.just(queueService.forwardToProduct(userId,productId));
    }
}