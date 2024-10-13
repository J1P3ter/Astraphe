package com.j1p3ter.queueserver.presentation.controller;

import com.j1p3ter.queueserver.application.dto.AllowResponseDto;
import com.j1p3ter.queueserver.application.dto.RankResponseDto;
import com.j1p3ter.queueserver.application.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/waiting_queue")
@Slf4j(topic = "Queue Controller")
public class QueueController {
    private final QueueService queueService;

    // 사용자를 대기열에 등록 / rank를 리턴
    @PostMapping("/{productId}")
    public Mono<RankResponseDto> registerUser(@RequestHeader(name = "X-USER-ID") Long userId,
                                              @PathVariable Long productId){
        return queueService.registerWaitQueue(userId,productId)
                .map(rank -> new RankResponseDto(userId,rank));
    }

    // 사용자 count 명을 접근 허용
    // 대기열 queue에서 삭제 >
    @PostMapping("/{productId}/allow")
    public Mono<AllowResponseDto> allowUser(@PathVariable Long productId, Long count){
        return queueService.allowUser(productId,count)
                .map(allowed -> new AllowResponseDto(count,allowed));
    }

    // 사용자 진입 가능 확인
    // 진입 가능 > 타깃 페이지
    // 진입 불가 > 대기열에 등록
    @GetMapping("/{productId}/isAllowed")
    public Mono<?> isAllowedUser(@RequestHeader(name = "X-USER-ID") Long userId,
                                 @PathVariable Long productId){
        return queueService.isAllowed(userId,productId);
    }

}
