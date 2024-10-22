package com.j1p3ter.queueserver.application.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.queueserver.application.dto.ProductResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@FeignClient(name = "product-server")
public interface ProductClient {
    @GetMapping("/api/products/{productId}")
    ApiResponse<ProductResponseDto> getProduct(@PathVariable Long productId);

    // 동기 호출을 비동기로 감싸고 ApiResponse에서 데이터를 추출하는 메소드
    default Mono<ProductResponseDto> forwardToProduct(Long productId) {
        return Mono.fromCallable(() -> getProduct(productId))
                .map(apiResponse -> apiResponse.getData())  // ApiResponse에서 data 필드 추출
                .subscribeOn(Schedulers.boundedElastic());
    }
}