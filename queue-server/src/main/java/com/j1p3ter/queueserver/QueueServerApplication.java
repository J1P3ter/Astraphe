package com.j1p3ter.queueserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        scanBasePackages = {
                "com.j1p3ter.queueserver",
                "com.j1p3ter.common.response",
                "com.j1p3ter.common.exception"
        }
)
@EnableScheduling
@EnableDiscoveryClient
public class QueueServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(QueueServerApplication.class, args);
    }

}