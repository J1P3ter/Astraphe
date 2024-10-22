package com.j1p3ter.queueserver.application.service;

import com.j1p3ter.queueserver.config.QueueScheduler;
import com.j1p3ter.queueserver.presentation.request.StartAllowRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ScheduledQueue Service")
public class ScheduledQueueService {
    private final QueueScheduler queueScheduler;

    public Mono<String> startScheduling(Long productId, StartAllowRequestDto requestDto) {
        queueScheduler.scheduleTask(productId, requestDto.getCount(), requestDto.getDelay());
        return Mono.just("스케줄링 시작 완료");
    }

    public Mono<String> stopScheduling(Long productId) {
        queueScheduler.stopTask(productId);
        return Mono.just("스케줄링 종료 완료");
    }
}
