package com.j1p3ter.productserver.application.dto.subscribeProduct;

import com.j1p3ter.productserver.domain.subscribeProduct.SubscribeProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeProductResponseDto {
    private Long subscribeProductId;
    private Long productId;
    private Long userId;
    public SubscribeProductResponseDto(SubscribeProduct subscribeProduct) {
        this.subscribeProductId = subscribeProduct.getId();
        this.productId = subscribeProduct.getProduct().getId();
        this.userId = subscribeProduct.getUserId();
    }
}
