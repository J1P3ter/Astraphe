package com.j1p3ter.productserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

@SpringBootApplication(
        scanBasePackages = {
                "com.j1p3ter.productserver",
                "com.j1p3ter.common.auditing",
                "com.j1p3ter.common.response",
                "com.j1p3ter.common.exception",
                "com.j1p3ter.common.config.global"
        },
        exclude = {
                RedisAutoConfiguration.class
        }
)
public class ProductServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServerApplication.class, args);
    }

}
