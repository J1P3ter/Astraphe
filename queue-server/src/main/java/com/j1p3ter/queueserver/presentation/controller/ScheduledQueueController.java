package com.j1p3ter.queueserver.presentation.controller;

import com.j1p3ter.queueserver.application.service.ScheduledQueueService;
import com.j1p3ter.queueserver.presentation.request.StartAllowRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/schedulingQueue")
@Slf4j(topic = "Scheduling Queue Controller")
public class ScheduledQueueController {
    private ScheduledQueueService scheduledQueueService;
    @Operation(summary = "특정 productId의 allow 스케줄링 시작")
    @PostMapping("/{productId}/startAllow")
    public Mono<String> startScheduling(@RequestHeader(name = "X-USER-ID") Long userId,
                                        @PathVariable Long productId, @RequestBody StartAllowRequestDto requestDto){
        return scheduledQueueService.startScheduling(productId, requestDto);
    }

    @Operation(summary = "특정 productId의 allow 스케줄링 정지")
    @PostMapping("/{productId}/stopAllow")
    public Mono<String> stopScheduling(@RequestHeader(name = "X-USER-ID") Long userId,
                                              @PathVariable Long productId){
        return scheduledQueueService.stopScheduling(productId);
    }
}
