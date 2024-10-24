package com.j1p3ter.orderserver.application.dto.order;

import com.j1p3ter.orderserver.domain.order.Order;
import com.j1p3ter.orderserver.domain.order.OrderState;
import com.j1p3ter.orderserver.domain.payment.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class OrderResponseDto {

    private Long orderId;                 // 주문 ID
    private Integer totalPrice;              // 총 주문 금액
    private PaymentStatus paymentStatus;         // 결제 수단 (ENUM으로 사용 가능)
    private Integer deliveryPrice;        // 배송비
    private LocalDateTime deliveryDate;   // 배송일
    private String cancelReason;          // 취소 사유
    private String memo;                  // 요청 사항
    private OrderState state;                 // 주문 상태 (ENUM으로 사용 가능)
    private Long userId;                  // 사용자 ID
    private Long addressId;               // 배송지 ID
    private List<OrderDetailDto> orderDetails; // 주문 상품 리스트 (OrderDetailDto 포함)

    public static OrderResponseDto fromEntity(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .totalPrice(order.getTotalPrice())
                .paymentStatus(order.getPaymentStatus())
                .deliveryPrice(order.getDeliveryPrice())
                .deliveryDate(order.getDeliveryDate())
                .cancelReason(order.getCancelReason())
                .memo(order.getMemo())
                .state(order.getState())
                .userId(order.getUserId())
                .addressId(order.getAddressId())
                .orderDetails(order.getOrderDetails().stream()
                        .distinct()  // 중복 제거
                        .map(orderDetail -> OrderDetailDto.fromEntity(orderDetail))
                        .toList())
                .build();
    }


}
