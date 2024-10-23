package com.j1p3ter.orderserver.application.service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentCreateRequestDto {
    private Long userId;
    private Long orderId;
    private Long paymentId;
    private String paymentKey;
    private Integer amount;
    private String newStatus;
}
