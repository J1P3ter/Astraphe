package com.j1p3ter.orderserver.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    PENDING("결제 대기"),
    APPROVED("결제 승인"),
    FAILED("결제 실패");

    private final String title;
}