package com.j1p3ter.queueserver;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import java.io.IOException;

@TestConfiguration
public class EmbeddedRedis {
    private final RedisServer redisServer;

    // docker container의 포트 번호와 중복을 방지하기 위해 임의의 포트 설정
    public EmbeddedRedis() throws IOException {
        this.redisServer = new RedisServer(63790);
    }

    // bean 생성 후 최초 1회 실행
    @PostConstruct
    public void start() throws IOException {
        this.redisServer.start();
    }

    // bean 제거 직전 1회 실행
    @PreDestroy
    public void stop() throws IOException {
        this.redisServer.stop();
    }
}