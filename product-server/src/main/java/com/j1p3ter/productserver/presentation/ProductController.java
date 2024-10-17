package com.j1p3ter.productserver.presentation;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.productserver.application.ProductService;
import com.j1p3ter.productserver.application.dto.DirectionType;
import com.j1p3ter.productserver.application.dto.SortType;
import com.j1p3ter.productserver.application.dto.product.ProductCreateRequestDto;
import com.j1p3ter.productserver.application.dto.product.ProductUpdateRequestDto;
import com.j1p3ter.productserver.config.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product API")
@Slf4j(topic = "Product Controller")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create Product")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> createProduct(
            @RequestHeader(name = "X-USER-ID") Long userId,
            @RequestPart MultipartFile productImg,
            @RequestPart MultipartFile productDescriptionImg,
            @RequestPart ProductCreateRequestDto productCreateRequestDto
    ){
        if (!FileValidator.isImageFileValid(productImg) || !FileValidator.isImageFileValid(productDescriptionImg)) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid image file format. Only JPG, PNG, and JPEG are allowed.");
        }
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
            @Parameter(description = "(CREATED_AT - default, UPDATED_AT, DISCOUNTED_PRICE)")
            @RequestParam(defaultValue = "CREATED_AT", name = "sort") SortType sort,
            @Parameter(description = "(DESC - default, ASC)")
            @RequestParam(defaultValue = "DESC", name = "direction") DirectionType direction
    ){
        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.fromString(direction.name()), sort.getValue()));
        return ApiResponse.success(productService.searchProduct(companyName, productName, categoryCode, pageable));
    }
    

    @Operation(summary = "Update Product Info")
    @PutMapping("/{productId}")
    public ApiResponse<?> updateProduct(
            @RequestHeader(name = "X-USER-ID") Long userId,
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequestDto productUpdateRequestDto
    ){
        try{
            return ApiResponse.success(productService.updateProduct(userId, productId, productUpdateRequestDto));
        }catch(OptimisticLockingFailureException | StaleObjectStateException e){
            throw new ApiException(HttpStatus.BAD_REQUEST, "데이터에 동시에 접근할 수 없습니다.", e.getMessage());
        }
    }

    @Operation(summary = "Delete Product")
    @DeleteMapping("/{productId}")
    public ApiResponse<?> deleteProduct(
            @RequestHeader(name = "X-USER-ID") Long userId,
            @PathVariable Long productId
    ){
        try{
            return ApiResponse.success(productService.deleteProduct(userId, productId));
        }catch(OptimisticLockingFailureException | StaleObjectStateException e){
            throw new ApiException(HttpStatus.BAD_REQUEST, "데이터에 동시에 접근할 수 없습니다.", e.getMessage());
        }
    }

}
