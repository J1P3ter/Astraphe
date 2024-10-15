package com.j1p3ter.orderserver.application.dto;

import com.j1p3ter.orderserver.domain.order.OrderState;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@AllArgsConstructor
public class UpdateOrderDto {
    @NotNull
    private OrderState orderStatus;
}
