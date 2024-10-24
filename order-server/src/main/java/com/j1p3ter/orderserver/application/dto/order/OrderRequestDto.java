package com.j1p3ter.orderserver.application.dto.order;

import com.j1p3ter.orderserver.domain.order.Order;
import com.j1p3ter.orderserver.domain.order.OrderDetail;
import com.j1p3ter.orderserver.domain.order.OrderState;
import com.j1p3ter.orderserver.domain.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderRequestDto {

    private Integer totalPrice;          // 총 주문 금액
    private PaymentStatus paymentStatus;     // 결제 수단 (ENUM)
    private Integer deliveryPrice;    // 배송비
    private LocalDateTime deliveryDate; // 배송일
    private String cancelReason;      // 취소 사유
    private String memo;              // 요청사항
    private OrderState state;             // 주문 상태 (ENUM)
    private Long userId;              // 사용자 ID
    private Long addressId;           // 배송지 ID
    private List<OrderDetail> orderDetails;

    public Order CreateOrderRequestDto(Long userId) {
        return Order.builder()
                .totalPrice(this.totalPrice)
                .paymentStatus(this.paymentStatus)
                .deliveryPrice(this.deliveryPrice)
                .deliveryDate(this.deliveryDate)
                .cancelReason(this.cancelReason)
                .memo(this.memo)
                .state(this.state)
                .userId(userId)
                .addressId(this.addressId)
                .orderDetails(orderDetails)
                .build();
    }
}
