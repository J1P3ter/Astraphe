package com.j1p3ter.orderserver.application.dto.order;

import com.j1p3ter.orderserver.domain.order.OrderState;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateOrderDto {
    @NotNull
    private OrderState orderStatus;
}
