package com.j1p3ter.queueserver.application.client;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.queueserver.application.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-server")
public interface ProductClient {
    @GetMapping("/api/products/{productId}")
    ApiResponse<ProductResponseDto> getProduct(@PathVariable Long productId);
}
