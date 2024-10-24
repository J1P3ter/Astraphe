package com.j1p3ter.queueserver.application.service;

import java.time.Instant;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.queueserver.application.client.ProductClient;
import com.j1p3ter.queueserver.application.dto.ProductResponseDto;
import com.j1p3ter.queueserver.application.dto.RankResponseDto;
import com.j1p3ter.queueserver.config.QueueJwtUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Queue Service")
public class QueueService {
    private final ReactiveRedisTemplate<String,Object> reactiveRedisTemplate;

    // redis 저장 시 key의 형식
    private final String QUEUE_WAIT_KEY = "queue:%s:wait";
    private final String QUEUE_WAIT_KEY_FOR_SCAN = "queue:*:wait"; // all keys
    private final String QUEUE_PROCEED_KEY = "queue:%s:proceed";

    // Queue Token 발행
    private final QueueJwtUtil queueJwtUtil;

    private final ProductClient productClient;

    // 사용자 대기열에 추가 후 rank를 반환
    public Mono<Long> registerWaitQueue(Long userId, Long productId) {
        // redis Sorted Set
        // key : queue:productId
        // field : userId.toString()
        // score : unix timestamp
        isProductExists(productId); // product 존재 여부 확인

        String queueKey = QUEUE_WAIT_KEY.formatted(productId); // productId 별로 구분
        long unixTimestamp = Instant.now().getEpochSecond(); // 진입 시간

        return reactiveRedisTemplate.opsForZSet().rank(queueKey, userId.toString()) // 중복 확인
                .defaultIfEmpty(-1L) // rank가 없을 경우 기본값 설정
                .flatMap(rank -> {
                    if (rank != -1) {
                        // 이미 등록된 사용자인 경우: 기존 등록 삭제 후 재등록
                        return reactiveRedisTemplate.opsForZSet().remove(queueKey, userId.toString())
                                .then(reactiveRedisTemplate.opsForZSet().add(queueKey, userId.toString(), unixTimestamp))
                                .then(reactiveRedisTemplate.opsForZSet().rank(queueKey, userId.toString()))
                                .doOnNext(rank3 -> log.info("Remove and Add : User rank in queue: {}", rank3))
                                .map(rank1 -> rank1 != null ? rank1 + 1 : -1);
                    } else {
                        // 등록되지 않은 사용자
                        return reactiveRedisTemplate.opsForZSet().add(queueKey, userId.toString(), unixTimestamp)
                                .then(reactiveRedisTemplate.opsForZSet().rank(queueKey, userId.toString()))
                                .doOnNext(rank2 -> log.info("Add : User rank in queue: {}", rank2))
                                .map(rank2 -> rank2 != null ? rank2 + 1 : -1);
                    }
                });
    }

    // productId 별로 count 명의 유저 허용 후 진입한 유저 인원수 리턴
    public Mono<Long> allowUser(Long productId, Long count) {
        isProductExists(productId); // product 존재 여부 확인
        return // waiting Queue에서 timestamp 기준 작은 순서대로 count 개의 값을 pop
                reactiveRedisTemplate.opsForZSet().popMin(QUEUE_WAIT_KEY.formatted(productId), count)
                        // pop한 값 proceed Queue에 추가
                        .flatMap(member -> reactiveRedisTemplate.opsForZSet().add(QUEUE_PROCEED_KEY.formatted(productId), member.getValue(), Instant.now().getEpochSecond()))
                        .doOnNext(success -> log.info("User " + success + " added to proceed queue"))
                        .count();
    }

    // user 확인 후
    public Mono<Boolean> isAllowed(Long userId, Long productId) {
        isProductExists(productId); // product 존재 여부 확인
        return reactiveRedisTemplate.opsForZSet().rank(QUEUE_PROCEED_KEY.formatted(productId),userId.toString())
                .map(rank -> rank != null? true : false) // rank가 null이 아니면 proceed queue에 존재
                .defaultIfEmpty(false);
    }

    // 사용자의 대기 번호 반환
    public Mono<Long> getRank(Long userId, Long productId){
        isProductExists(productId); // product 존재 여부 확인
        return reactiveRedisTemplate.opsForZSet().rank(QUEUE_WAIT_KEY.formatted(productId), userId.toString())
                .defaultIfEmpty(-1L) // 대기 번호 없으면 -1 리턴
                .map(rank -> rank >= 0 ? rank + 1 : rank); // 1부터 리턴
    }

    // 특정 product의 대기 인원 출력
    public Mono<Long> getWaitingUsers(Long productId){
        isProductExists(productId); // product 존재 여부 확인
        return reactiveRedisTemplate.opsForZSet().size(QUEUE_WAIT_KEY.formatted(productId));
    }

    // 특정 product의 대기 목록 출력
    public Flux<RankResponseDto> getQueueList(Long productId) {
        isProductExists(productId); // product 존재 여부 확인
        Range<Long> range = Range.closed(0L, -1L);
        AtomicLong i = new AtomicLong(1);
        return reactiveRedisTemplate.opsForZSet()
                .range(QUEUE_WAIT_KEY.formatted(productId), range)
                .flatMap(member -> Mono.just(new RankResponseDto(Long.parseLong(member.toString()), i.getAndIncrement())));
    }

//     AllowToken 발급
    public String createToken(Long userId, Long productId) {
        isProductExists(productId); // product 존재 여부 확인
        if (!isAllowed(userId, productId).block()) { // .block() 동기적 처리 > isAllowed 연산 끝난 뒤 검사
            return "접근이 허용되지 않은 유저입니다.";
        }

        String accessToken = queueJwtUtil.AUTHORIZATION_HEADER + ": " + queueJwtUtil.createToken(userId,productId);
        return accessToken;
    }

    private void isProductExists(Long productId){
        try {
            ApiResponse<ProductResponseDto> response = productClient.getProduct(productId);
        } catch (FeignException.NotFound e) {
            // 404 Not Found 시 에러 처리
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found", e);
        }
    }
}