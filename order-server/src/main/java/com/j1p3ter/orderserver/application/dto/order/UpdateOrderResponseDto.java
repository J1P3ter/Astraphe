package com.j1p3ter.orderserver.application.dto.order;

import com.j1p3ter.orderserver.domain.order.OrderState;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateOrderResponseDto {
    private Long orderId;
    private OrderState orderStatus;
}
