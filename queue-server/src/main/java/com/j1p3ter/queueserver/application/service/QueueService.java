package com.j1p3ter.queueserver.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j(topic = "Queue Service")
public class QueueService {

    // 성공 여부를 반환
    // 대기열에 key:Id, value: timestamp로 저장
    public Mono<?> registerWaitQueue(Long userId) {
        return null;
    }
}

// key를 사용해서 저장해야 한다