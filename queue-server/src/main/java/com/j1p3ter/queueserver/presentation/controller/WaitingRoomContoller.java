package com.j1p3ter.queueserver.presentation.controller;

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

    // rank 리턴
    @GetMapping("getRank/{productId}")
    public Mono<?> getRank(@RequestHeader(name = "X-USER-ID") Long userId,
                                 @PathVariable Long productId){
        return queueService.getRank(userId, productId)
                .map(rank -> new RankResponseDto(userId,rank));
    }


    // 등록
    @GetMapping("/{productId}")
    public Mono<Boolean> handleQueueRequest(@RequestHeader(name = "X-USER-ID") Long userId,
                                            @PathVariable Long productId) {
        return queueService.isAllowed(userId, productId)
                .filter(allowed -> allowed) // 허용된 경우
                .switchIfEmpty(queueService.registerWaitQueue(userId, productId) // 허용되지 않은 경우 대기열에 등록
                        .onErrorResume(ex -> queueService.getRank(userId,productId)) // 에러 발생 시 rank 리턴
                        .map(rank -> false)) // 등록 완료 후 false 반환
                .defaultIfEmpty(true); // 허용된 경우 true 반환
    }
}
