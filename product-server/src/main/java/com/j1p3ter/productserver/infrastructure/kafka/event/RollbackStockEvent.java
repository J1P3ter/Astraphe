package com.j1p3ter.productserver.infrastructure.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RollbackStockEvent {
    private Integer retryCount;
    private Long orderId;
    private Long productId;
    private Integer quantity;

    // 롤백 실패시 재시도 횟수를 증가하기 위한 메서드
    public void increaseRetryCount() {
        this.retryCount++;
    }
}
