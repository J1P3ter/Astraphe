package com.j1p3ter.orderserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.j1p3ter.orderserver",
                "com.j1p3ter.common.auditing",
                "com.j1p3ter.common.response",
                "com.j1p3ter.common.exception"
        }
)
public class OrderServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServerApplication.class, args);
    }

}
