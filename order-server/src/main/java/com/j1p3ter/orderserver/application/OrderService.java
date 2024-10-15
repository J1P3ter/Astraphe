package com.j1p3ter.orderserver.application;

import com.j1p3ter.common.exception.ApiException;
import com.j1p3ter.common.response.ApiResponse;
import com.j1p3ter.orderserver.application.client.product.ProductClient;
import com.j1p3ter.orderserver.application.client.product.dto.ProductResponseDto;
import com.j1p3ter.orderserver.application.dto.OrderRequestDto;
import com.j1p3ter.orderserver.application.dto.OrderResponseDto;
import com.j1p3ter.orderserver.domain.order.*;
import com.j1p3ter.orderserver.infrastructure.kafka.EventSerializer;
import com.j1p3ter.orderserver.infrastructure.kafka.event.ReduceStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderDetailRepository orderDetailRepository;

    @Transactional
    public OrderResponseDto createOrder(Long userId, OrderRequestDto orderRequestDto) {
        Order order = orderRequestDto.CreateOrderRequestDto(userId);
        List<OrderDetail> orderDetails = orderRequestDto.getOrderDetails();

        //orderDetails -> product 1, 2, 3
        // 2. 각 주문된 상품에 대해 유효성 검사 및 처리
        orderDetails
                .forEach(orderDetail -> {
                    ApiResponse<ProductResponseDto> response = productClient.getProduct(orderDetail.getProductId());
                    ProductResponseDto product = response.getData();

                    // 2.2. 재고 확인
                    Integer productStock = product.getStock();
                    if (productStock == null) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "상품의 재고 정보가 없습니다.", "INSUFFICIENT_STOCK");
                    }

                    // 재고 확인
                    if (productStock < orderDetail.getQuantity()) {
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

        List<OrderDetail> cp = new ArrayList<>(orderDetails);
        // 여기선 사이즈 한 개
        cp.forEach(order::addOrderProduct);
        // 여기선 사이즈 두 개

        // 3. 주문 저장
        Order savedOrder = orderRepository.save(order);
        // 3.1 - Order Detail 저장
        orderDetailRepository.saveAll(orderDetails);
        // payload
        orderDetails.forEach(orderDetail -> {
            // ReduceStockEvent 생성
            ReduceStockEvent event = new ReduceStockEvent(savedOrder.getOrderId(), orderDetail.getProductId(), orderDetail.getQuantity());

            // Kafka 메시지 생성
            Message<String> kafkaMessage = MessageBuilder
                    .withPayload(EventSerializer.serialize(event))  // 이벤트 직렬화
                    .setHeader(KafkaHeaders.TOPIC, "reduce-stock")  // 토픽 설정
                    .setHeader("X-USER-ID", userId)                 // 사용자 ID 헤더 설정
                    .build();

            // Kafka 메시지 전송
            kafkaTemplate.send(kafkaMessage);
        });
        // 4. 주문 응답 DTO 생성
        return OrderResponseDto.fromEntity(savedOrder);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderState newStatus, Long userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "해당 하는 주문이 없습니다", "NOT_FOUND"));
        order.update(newStatus);
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

    public String deleteOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "해당 하는 주문이 없습니다", "NOT_FOUND"));
        OrderDetail orderDetail = orderDetailRepository.findByOrderId(orderId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "해당 하는 주문이 없습니다", "NOT_FOUND"));

        if (!Objects.equals(order.getUserId(), userId))
            throw new ApiException(HttpStatus.FORBIDDEN, "주문 정보 확인 바랍니다.", "FORBIDDEN");
        try {
            order.softDelete(orderId);
            orderDetail.softDelete(orderId);
            return "주문 삭제 완료";
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 삭제에 실패했습니다.", e.getMessage());
        }
    }
}