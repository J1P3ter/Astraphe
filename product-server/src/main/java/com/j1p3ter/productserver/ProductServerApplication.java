package com.j1p3ter.productserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.j1p3ter.productserver",
                "com.j1p3ter.common.auditing",
                "com.j1p3ter.common.response",
                "com.j1p3ter.common.exception",
                "com.j1p3ter.common.config.global"
        }
)
public class ProductServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServerApplication.class, args);
    }

}
