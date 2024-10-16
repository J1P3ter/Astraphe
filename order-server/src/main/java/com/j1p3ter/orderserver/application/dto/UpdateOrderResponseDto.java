package com.j1p3ter.orderserver.application.dto;

import com.j1p3ter.orderserver.domain.order.OrderState;
import lombok.*;

@Builder
@Getter
public class UpdateOrderResponseDto {
    private Long orderId;
    private OrderState orderStatus;
}
