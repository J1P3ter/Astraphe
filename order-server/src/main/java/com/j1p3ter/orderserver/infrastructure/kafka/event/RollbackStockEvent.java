package com.j1p3ter.orderserver.infrastructure.kafka.event;

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
}
