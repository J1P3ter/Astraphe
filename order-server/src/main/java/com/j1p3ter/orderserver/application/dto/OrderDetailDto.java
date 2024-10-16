package com.j1p3ter.orderserver.application.dto;

import com.j1p3ter.orderserver.domain.order.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {

    private Long productId;          // 상품 ID
    private String productName;      // 상품명
    private Integer quantity;        // 주문 수량
    private Integer originalPrice;   // 상품 원래 가격
    private Integer discountedPrice; // 상품 할인된 가격
    private String productOption;    // 상품 옵션
    private Short categoryCode;      // 카테고리 코드
    private Long companyId;          // 업체 ID

    public static OrderDetailDto fromEntity(OrderDetail orderDetail) {
        return OrderDetailDto.builder()
                .productId(orderDetail.getProductId())
                .productName(orderDetail.getProductName())
                .quantity(orderDetail.getQuantity())
                .originalPrice(orderDetail.getOriginalPrice())
                .discountedPrice(orderDetail.getDiscountedPrice())
                .productOption(orderDetail.getProductOption())
                .categoryCode(orderDetail.getCategoryCode())
                .companyId(orderDetail.getCompanyId())
                .build();
    }
}

