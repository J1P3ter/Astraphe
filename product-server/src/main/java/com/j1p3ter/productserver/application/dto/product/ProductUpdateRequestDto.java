package com.j1p3ter.productserver.application.dto.product;

import com.j1p3ter.productserver.domain.company.Company;
import com.j1p3ter.productserver.domain.product.Category;
import com.j1p3ter.productserver.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDto {

    private String productName;
    private String description;
    private Integer originalPrice;
    private Integer discountedPrice;
    private Integer stock;
    private List<ProductOptionDto> productOptions;
    private Long categoryCode;
    private LocalDateTime saleStartTime;
    private LocalDateTime saleEndTime;

}
