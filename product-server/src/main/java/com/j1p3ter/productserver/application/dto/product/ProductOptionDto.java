package com.j1p3ter.productserver.application.dto.product;

import com.j1p3ter.productserver.domain.product.Product;
import com.j1p3ter.productserver.domain.product.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionDto {
    private String optionName;
    private String optionValue;
    private Integer optionPrice;

    public ProductOption toEntity(Product product){
        return ProductOption.builder()
                .product(product)
                .optionName(this.optionName)
                .optionValue(this.optionValue)
                .optionPrice(this.optionPrice)
                .build();
    }

    public static ProductOptionDto from(ProductOption productOption){
        return new ProductOptionDto(productOption.getOptionName(), productOption.getOptionValue(), productOption.getOptionPrice());
    }
}
