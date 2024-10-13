package com.j1p3ter.orderserver.application.dto;

import com.j1p3ter.orderserver.application.client.product.dto.ProductResponseDto;
import com.j1p3ter.orderserver.domain.order.Order;
import com.j1p3ter.orderserver.domain.order.OrderState;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class OrderListResponseDto {

    private Long orderId;                      // 주문 ID
    private Long userId;                       // 사용자 ID
    private Integer totalPrice;                // 주문 총 금액
    private String memo;                       // 메모
    private OrderState orderStatus;            // 주문 상태 (ENUM)
    private List<ProductResponseDto> orderProducts;  // 주문 상품 목록 (ProductResponseDto 리스트)

    // 엔티티를 받아서 생성하는 생성자
    public OrderListResponseDto(Order order, List<ProductResponseDto> productList) {
        this.orderId = order.getOrderId();
        this.userId = order.getUserId();
        this.totalPrice = order.getTotalPrice();
        this.memo = order.getMemo();
        this.orderStatus = order.getState();
        this.orderProducts = productList;
    }
}
