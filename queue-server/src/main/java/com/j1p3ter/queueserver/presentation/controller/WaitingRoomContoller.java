package com.j1p3ter.queueserver.presentation.controller;

import com.j1p3ter.productserver.application.dto.product.ProductResponseDto;
import com.j1p3ter.productserver.domain.product.Product;
import com.j1p3ter.queueserver.application.client.ProductClient;
import com.j1p3ter.queueserver.application.dto.RankResponseDto;
import com.j1p3ter.queueserver.application.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/waiting_room")
@Slf4j(topic = "WaitingRoom Controller")
public class WaitingRoomContoller {
    private final QueueService queueService;
    private ProductClient productClient;

    // rank 리턴
    @GetMapping("getRank/{productId}")
    public Mono<?> getRank(@RequestHeader(name = "X-USER-ID") Long userId,
                                 @PathVariable Long productId){
        return queueService.getRank(userId, productId)
                .map(rank -> new RankResponseDto(userId,rank));
    }

    @GetMapping("/{productId}")
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
                                .map(rank -> "User registered with rank: " + rank);
                    }
                });
    }


}
