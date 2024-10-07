package com.j1p3ter.common.config.global;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfigurer implements WebMvcConfigurer {

    // TODO: prod 환경에서 localhost를 바꿔야 함.
    private final String[] ORIGIN_WHITE_LIST = {
            "http://localhost:18080",
            "http://localhost:18081",
            "http://localhost:18082",
            "http://localhost:18083",
            "http://localhost:18084"
    };

    /**
     * Web browser에서 요청 시 swagger-ui를 사용하기 위함.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(ORIGIN_WHITE_LIST)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*");
        WebMvcConfigurer.super.addCorsMappings(registry);
    }

}