package com.j1p3ter.queueserver.application.service;

import java.time.Instant;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.queueserver.application.dto.RankResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Queue Service")
public class QueueService {
    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    // redis 저장 시 key의 형식
    private final String QUEUE_WAIT_KEY = "queue:%s:wait";
    private final String QUEUE_WAIT_KEY_FOR_SCAN = "queue:*:wait"; // all keys
    private final String QUEUE_PROCEED_KEY = "queue:%s:proceed";


    // 사용자 대기열에 추가 후 rank를 반환
    public Mono<Long> registerWaitQueue(Long userId, Long productId) {
        // redis Sorted Set
        // key : queue:productId
        // field : userId.toString()
        // score : unix timestamp

        String queueKey = QUEUE_WAIT_KEY.formatted(productId); // productId 별로 구분
        long unixTimestamp = Instant.now().getEpochSecond(); // 진입 시간
        Map<String, Object> userEntry = new HashMap<>();
        userEntry.put("timestamp", unixTimestamp);

        // Sorted Set에 사용자 추가 (정렬된 대기열)
        return reactiveRedisTemplate.opsForZSet().add(queueKey, userId.toString(), unixTimestamp)
                .filter(i -> i) // 등록 성공 시 true, 실패 시 switchIfEmpty 실행
                .switchIfEmpty(Mono.error(new ApiException(HttpStatus.CONFLICT, "User is already registered.",
                        String.format("User ID: %d is already registered for Product ID: %d", userId, productId))))
                .flatMap(i -> reactiveRedisTemplate.opsForZSet().rank(queueKey, userId.toString())) // 사용자 순위 조회
                .map(rank -> rank != null ? rank + 1 : -1); // rank는 0부터 시작하므로 +1, 없으면 -1
    }

    // count 명의 유저 허용 후 진입한 유저 인원수 리턴
    public Mono<Long> allowUser(Long productId, Long count) {
        return // waiting Queue에서 timestamp 기준 작은 순서대로 count 개의 값을 pop
                reactiveRedisTemplate.opsForZSet().popMin(QUEUE_WAIT_KEY.formatted(productId), count)
                        // pop한 값 proceed Queue에 추가
                        .flatMap(member -> reactiveRedisTemplate.opsForZSet().add(QUEUE_PROCEED_KEY.formatted(productId), member.getValue(), Instant.now().getEpochSecond()))
                        .count();
    }

    // user 확인 후
    public Mono<Boolean> isAllowed(Long userId, Long productId) {
        return reactiveRedisTemplate.opsForZSet().rank(QUEUE_PROCEED_KEY.formatted(productId),userId)
                .map(rank -> rank != null) // rank가 null이 아니면 proceed queue에 존재
                .defaultIfEmpty(false);
    }

    // 사용자의 대기 번호 반환
    public Mono<Long> getRank(final Long userId, final Long productId){
        return reactiveRedisTemplate.opsForZSet().rank(QUEUE_WAIT_KEY.formatted(productId), userId.toString())
                .defaultIfEmpty(-1L) // 대기 번호 없으면 -1 리턴
                .map(rank -> rank >= 0 ? rank + 1 : rank); // 1부터 리턴
    }
}