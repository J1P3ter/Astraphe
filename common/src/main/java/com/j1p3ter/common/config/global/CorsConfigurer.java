package com.j1p3ter.common.config.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfigurer implements WebMvcConfigurer {

    @Value("${server.host}")
    private String host;

    private final String[] ORIGIN_WHITE_LIST = {
            "http://"+ host + ":18080",
            "http://"+ host + ":18081",
            "http://"+ host + ":18082",
            "http://"+ host + ":18083",
            "http://"+ host + ":18084"
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