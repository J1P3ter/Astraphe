package com.j1p3ter.orderserver.application.client.product.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Long productId;
    private Long companyId;
    private String companyName;
    private String productName;
    private String productImgUrl;
    private String description;
    private String descriptionImgUrl;
    private Integer originalPrice;
    private Integer discountedPrice;
    private Integer stock;
    private List<ProductOptionDto> productOptions;
    private Long categoryCode;
    private String categoryName;
    private LocalDateTime saleStartTime;
    private LocalDateTime saleEndTime;
}
