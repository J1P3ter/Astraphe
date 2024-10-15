package com.j1p3ter.userserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(
        scanBasePackages = {
                "com.j1p3ter.userserver",
                "com.j1p3ter.common.auditing",
                "com.j1p3ter.common.exception",
                "com.j1p3ter.common.response",
                "com.j1p3ter.common.config"
        }
)
@EnableDiscoveryClient
public class UserServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServerApplication.class, args);
    }

}