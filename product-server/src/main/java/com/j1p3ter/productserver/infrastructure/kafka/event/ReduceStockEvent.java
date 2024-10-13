package com.j1p3ter.productserver.infrastructure.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReduceStockEvent {
    private Long orderId;
    private Long productId;
    private Integer quantity;
}
