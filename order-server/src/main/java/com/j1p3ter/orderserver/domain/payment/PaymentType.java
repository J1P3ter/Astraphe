package com.j1p3ter.orderserver.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {
    TOSS_PAYMENTS("토스페이먼츠");

    private final String title;
}
