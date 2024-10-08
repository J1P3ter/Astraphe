package com.j1p3ter.queueserver;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

@SpringBootApplication(
        scanBasePackages = {
                "com.j1p3ter.queueserver",
                "com.j1p3ter.common.auditing",
                "com.j1p3ter.common.response",
                "com.j1p3ter.common.exception"
        }
)
public class QueueServerApplication {

   public static void main(String[] args) {
        SpringApplication.run(QueueServerApplication.class, args);
    }

}