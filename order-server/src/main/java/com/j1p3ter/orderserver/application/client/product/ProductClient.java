package com.j1p3ter.orderserver.application.client.product;

import com.j1p3ter.orderserver.application.client.product.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// ProductClient는 외부의 Product 마이크로서비스와 통신하는 역할을 담당
@FeignClient(name = "product-service")
public interface ProductClient extends ProductService {

    // 상품 정보를 가져오는 메서드
    @Override
    @GetMapping("/api/products/{productId}")
    ProductResponseDto getProduct(@PathVariable("productId") Long productId);
}

