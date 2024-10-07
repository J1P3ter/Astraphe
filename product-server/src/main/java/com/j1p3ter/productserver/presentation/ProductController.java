package com.j1p3ter.productserver.presentation;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.productserver.application.ProductService;
import com.j1p3ter.productserver.application.dto.company.CompanyCreateRequestDto;
import com.j1p3ter.productserver.application.dto.product.ProductCreateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product API")
@Slf4j(topic = "Product Controller")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create Product")
    @PostMapping
    public ApiResponse<?> createProduct(
            @RequestHeader(name = "X-USER-ID") Long userId,
            @RequestBody ProductCreateRequestDto productCreateRequestDto
    ){
        return ApiResponse.success(productService.createProduct(userId, productCreateRequestDto));
    }

}
