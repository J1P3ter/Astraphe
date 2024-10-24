package com.j1p3ter.orderserver.application.service.request;

public record TossPaymentConfirmRequestDto(
        Long orderId,
        String paymentKey,
        Integer amount
) {

}
