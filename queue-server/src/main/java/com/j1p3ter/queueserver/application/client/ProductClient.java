package com.j1p3ter.queueserver.application.client;

import com.j1p3ter.productserver.application.dto.product.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/products/{productId}")
    ProductResponseDto getProduct(@PathVariable Long productId);
}
