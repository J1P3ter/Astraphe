package com.j1p3ter.productserver.application;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.productserver.application.client.UserClient;
import com.j1p3ter.productserver.application.dto.product.ProductResponseDto;
import com.j1p3ter.productserver.application.dto.subscribeProduct.SubscribeProductResponseDto;
import com.j1p3ter.productserver.domain.product.Product;
import com.j1p3ter.productserver.domain.product.ProductRepository;
import com.j1p3ter.productserver.domain.subscribe.SubscribeProduct;
import com.j1p3ter.productserver.domain.subscribe.SubscribeProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SubscribeProduct Service")
public class SubscribeProductService {
    private final ProductRepository productRepository;
    private final SubscribeProductRepository subscribeProductRepository;
    private final UserClient userClient;

    public SubscribeProductResponseDto createSubscribeProduct(Long userId, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, "Product를 찾을 수 없습니다", "Product Not Found")
        );

        return new SubscribeProductResponseDto(subscribeProductRepository.save(new SubscribeProduct(userId,product)));
    }

    public SubscribeProductResponseDto getSubscribeProduct(Long subscribeProductId) {
        return new SubscribeProductResponseDto(subscribeProductRepository.findById(subscribeProductId).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, "subscribeProduct를 찾을 수 없습니다", "subscribeProduct Not Found")
        ));
    }

    public String deleteSubscribeProduct(Long userId, Long subscribeProductId) {
        if(!userId.equals(getSubscribeProduct(subscribeProductId).getUserId())){
            throw new ApiException(HttpStatus.FORBIDDEN,"본인의 구독만 삭제할 수 있습니다.","subscribeProduct is not User's");
        }
        subscribeProductRepository.deleteById(subscribeProductId);
        return "product 구독 취소 완료";
    }
}
