package com.j1p3ter.orderserver.application.client.product;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.orderserver.application.client.product.dto.ProductResponseDto;

public interface ProductService {
    ApiResponse<ProductResponseDto> getProduct(Long productId);

}
