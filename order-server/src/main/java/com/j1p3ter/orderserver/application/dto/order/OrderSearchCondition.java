package com.j1p3ter.orderserver.application.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderSearchCondition {

    private LocalDateTime createdAt; // 주문일 (주문 생성 시간)
    private String productName;      // 상품명 (검색할 상품 이름)
    private String state;            // 주문 상태 (ENUM으로도 사용 가능)

    // 기본 생성자, 전체 생성자 등 필요에 따라 추가 가능
}
