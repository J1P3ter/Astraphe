package com.j1p3ter.orderserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "com.j1p3ter.orderserver",
                "com.j1p3ter.common.auditing",
                "com.j1p3ter.common.response",
                "com.j1p3ter.common.exception"
        },
        exclude = {
                RedisAutoConfiguration.class
        }
)
@EnableFeignClients
public class OrderServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServerApplication.class, args);
    }

}
