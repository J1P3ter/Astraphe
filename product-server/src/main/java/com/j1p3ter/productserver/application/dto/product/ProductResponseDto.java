package com.j1p3ter.productserver.application.dto.product;

import com.j1p3ter.productserver.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
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

    public static ProductResponseDto from(Product product){
        return ProductResponseDto.builder()
                .productId(product.getId())
                .companyId(product.getCompany().getId())
                .companyName(product.getCompany().getCompanyName())
                .productName(product.getProductName())
                .description(product.getDescription())
                .originalPrice(product.getOriginalPrice())
                .discountedPrice(product.getDiscountedPrice())
                .stock(product.getStock())
                .productOptions(product.getProductOptions().stream().map(ProductOptionDto::from).toList())
                .categoryCode(product.getCategory().getCategoryCode())
                .categoryName(product.getCategory().getCategoryName())
                .saleStartTime(product.getSaleStartTime())
                .saleEndTime(product.getSaleEndTime())
                .build();


    }

}
