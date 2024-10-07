package com.j1p3ter.productserver.presentation;

import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.productserver.application.ProductService;
import com.j1p3ter.productserver.application.dto.product.ProductCreateRequestDto;
import com.j1p3ter.productserver.application.dto.product.ProductCreateRequestDto;
import com.j1p3ter.productserver.application.dto.product.ProductUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Operation(summary = "Get Product Info")
    @GetMapping("/{productId}")
    public ApiResponse<?> getProduct(
            @RequestHeader(name = "X-USER-ID", required = false) Long userId,
            @PathVariable Long productId
    ){
        return ApiResponse.success(productService.getProduct(productId));
    }

    @Operation(summary = "Search Product")
    @GetMapping
    public ApiResponse<?> searchProduct(
            @RequestHeader(name = "X-USER-ID", required = false) Long userId,
            @RequestParam(name = "companyName", required = false) String companyName,
            @RequestParam(defaultValue = "", name = "productName", required = false) String productName,
            @RequestParam(defaultValue = "0", name = "categoryCode", required = false) Long categoryCode,
            @RequestParam(defaultValue = "1", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @Parameter(description = "(createdAt, updatedAt, discountedPrice)") @RequestParam(defaultValue = "createdAt", name = "sort") String sort,
            @RequestParam(defaultValue = "DESC", name = "direction") String direction
    ){
        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.fromString(direction), sort));
        return ApiResponse.success(productService.searchProduct(companyName, productName, categoryCode, pageable));
    }
    

    @Operation(summary = "Update Product Info")
    @PutMapping("/{productId}")
    public ApiResponse<?> updateProduct(
            @RequestHeader(name = "X-USER-ID") Long userId,
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequestDto productUpdateRequestDto
    ){
        return ApiResponse.success(productService.updateProduct(userId, productId, productUpdateRequestDto));
    }

    @Operation(summary = "Delete Product")
    @DeleteMapping("/{productId}")
    public ApiResponse<?> deleteProduct(
            @RequestHeader(name = "X-USER-ID") Long userId,
            @PathVariable Long productId
    ){
        return ApiResponse.success(productService.deleteProduct(userId, productId));
    }

}
