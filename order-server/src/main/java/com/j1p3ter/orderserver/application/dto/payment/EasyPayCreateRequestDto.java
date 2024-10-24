package com.j1p3ter.orderserver.application.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record EasyPayCreateRequestDto(

        @NotNull(message = "userId는 필수입니다.")
        Long userId,

        @PositiveOrZero(message = "amount는 0 이상이어야 합니다.")
        Integer amount,

        @Pattern(regexp = "^[0-9]{6}$", message = "password는 6자리 숫자여야 합니다.")
        String password
) {

}
