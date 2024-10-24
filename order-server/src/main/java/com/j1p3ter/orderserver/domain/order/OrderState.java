package com.j1p3ter.orderserver.domain.order;

public enum OrderState {
    PENDING,         // 보류 (대기 중)
    ORDER_CREATED, // 재고 확인 완료
    PAYMENT_SUCCESS, // 결제 성공
    PAYMENT_FAILED,  // 결제 실패
    ORDER_COMPLETED, // 주문 완료
    ORDER_FAILED     // 주문 실패
}

