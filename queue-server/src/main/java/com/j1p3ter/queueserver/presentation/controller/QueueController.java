package com.j1p3ter.queueserver.presentation.controller;

import com.j1p3ter.queueserver.application.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/waiting_queue")
@Slf4j(topic = "Company Controller")
public class QueueController {
    private final QueueService queueService;

    // 대기열에 등록할 수 있는 API path
    @PostMapping
    public Mono<?> registerUser(@RequestHeader(name = "X-USER-ID") Long userId){
        return queueService.registerWaitQueue(userId);
    }
}
