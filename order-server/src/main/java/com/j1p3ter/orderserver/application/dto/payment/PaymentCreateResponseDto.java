package com.j1p3ter.orderserver.application.dto.payment;

public record PaymentCreateResponseDto(
        Boolean isPaymentSuccess,
        Long paymentId
) {

}
