package com.j1p3ter.queueserver.application.service;

import com.j1p3ter.queueserver.application.client.ProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.util.function.Tuples;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j(topic = "Scheduled Queue Service")
public class ScheduledQueueService {
    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;
    private final QueueService queueService;
    private final String QUEUE_WAIT_KEY_FOR_SCAN = "queue:*:wait"; // all keys
//
//    @Scheduled(fixedRate=1000)
//    public void work(){
//        log.info("AllowUser Scheduiling");
//        Long maxAllowUserCount = 3L;
//
//        reactiveRedisTemplate.scan(ScanOptions.scanOptions()
//                        .match(QUEUE_WAIT_KEY_FOR_SCAN)
//                        .count(100)
//                        .build())
//                .map(key -> key.split(":")[2])
//                .flatMap(queue -> queueService.allowUser(, maxAllowUserCount).map(allowed -> Tuples.of(queue, allowed)))
//                .doOnNext(tuple -> log.info("Tried %d and allowed %d members of %s queue".formatted(maxAllowUserCount, tuple.getT2(), tuple.getT1())))
//                .subscribe();
//
//    }
}
