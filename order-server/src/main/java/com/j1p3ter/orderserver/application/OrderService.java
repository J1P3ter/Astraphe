package com.j1p3ter.orderserver.application;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.orderserver.application.client.product.ProductClient;
import com.j1p3ter.orderserver.application.client.product.dto.ProductResponseDto;
import com.j1p3ter.orderserver.application.dto.OrderRequestDto;
import com.j1p3ter.orderserver.application.dto.OrderResponseDto;
import com.j1p3ter.orderserver.domain.order.Order;
import com.j1p3ter.orderserver.domain.order.OrderDetail;
import com.j1p3ter.orderserver.domain.order.OrderRepository;
import com.j1p3ter.orderserver.domain.order.OrderState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    @Transactional
    public OrderResponseDto createOrder(Long userId, OrderRequestDto orderRequestDto) {
        Order order = orderRequestDto.CreateOrderRequestDto(userId);
        List<OrderDetail> orderDetails = order.getOrderDetails();
        //orderDetails -> product 1, 2, 3
        // 2. 각 주문된 상품에 대해 유효성 검사 및 처리
        orderDetails.stream()
                .forEach(orderDetail -> {
                    ProductResponseDto product = productClient.getProduct(orderDetail.getProductId());

//                    // 2.1. 상품 존재 여부 확인
//                    if (product == null) {
//                        throw new ApiException(HttpStatus.NOT_FOUND, "Product를 찾을 수 없습니다.", "PRODUCT_NOT_FOUND");
//                    }
                    // 2.2. 재고 확인
                    if (product.getStock() < orderDetail.getQuantity()) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "상품 재고가 부족합니다.", "INSUFFICIENT_STOCK");
                    }

                    // 2.3. 상품 수량 확인
                    if (orderDetail.getQuantity() < 0) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "주문 수량이 0 이하입니다.", "INVALID_QUANTITY");
                    }

                    // 2.4. 판매 기간 확인
                    LocalDateTime currentTime = LocalDateTime.now();
                    if (currentTime.isBefore(product.getSaleStartTime()) || currentTime.isAfter(product.getSaleEndTime())) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "해당 상품은 현재 판매 중이 아닙니다.", "INVALID_SALE_PERIOD");
                    }
                });

        // 3. 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 4. 주문 응답 DTO 생성
        return OrderResponseDto.fromEntity(savedOrder);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderState newStatus, Long userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "해당 하는 주문이 없습니다", "NOT_FOUND"));
        order.update(newStatus);
        return OrderResponseDto.fromEntity(order);
    }

    @Transactional
    public OrderResponseDto deleteOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "해당 하는 주문이 없습니다", "NOT_FOUND"));
        order.delete(userId);
        return OrderResponseDto.fromEntity(order);
    }

    public OrderResponseDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "해당 하는 주문이 없습니다", "NOT_FOUND"));
        return OrderResponseDto.fromEntity(order);
    }

    public Page<OrderResponseDto> searchOrder(String productName, OrderState state, Pageable pageable) {
        Page<Order> orders;

        // 조건이 하나라도 있을 경우, 필터링된 결과를 조회
        if ((productName != null && !productName.isEmpty()) || state != null) {
            orders = orderRepository.findAllByIsDeletedFalseAndKeyword(productName, state, pageable);
        } else {
            orders = orderRepository.findAllByIsDeletedFalse(pageable);
        }
        return orders.map(OrderResponseDto::fromEntity);
    }
}