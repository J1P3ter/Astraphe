package com.j1p3ter.orderserver.application.client.product;

import com.j1p3ter.orderserver.application.client.product.dto.ProductResponseDto;

public interface ProductService {
    ProductResponseDto getProduct(Long productId);

}
