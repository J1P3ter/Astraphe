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
public class ProductCreateRequestDto {

    private Long companyId;
    private String productName;
    private String description;
    private Integer originalPrice;
    private Integer discountedPrice;
    private Integer stock;
    private List<ProductOptionDto> productOptions;
    private Long categoryCode;
    private LocalDateTime saleStartTime;
    private LocalDateTime saleEndTime;

    public Product toEntity(Company company, Category category){
        return Product.builder()
                .company(company)
                .productName(this.productName)
                .description(this.description)
                .originalPrice(this.originalPrice)
                .discountedPrice(this.discountedPrice)
                .stock(this.stock)
                .category(category)
                .productOptions(new ArrayList<>())
                .saleStartTime(this.saleStartTime)
                .saleEndTime(this.saleEndTime)
                .rate(0.0)
                .isHidden(false)
                .isSoldout(this.stock <= 0)
                .build();
    }
}
