package com.j1p3ter.orderserver.application.service;

import com.j1p3ter.orderserver.application.dto.order.OrderResponseDto;
import com.j1p3ter.orderserver.application.dto.payment.PaymentCreateResponseDto;
import com.j1p3ter.orderserver.application.service.request.TossPaymentCreateRequestDto;
import com.j1p3ter.orderserver.domain.order.OrderState;
import com.j1p3ter.orderserver.domain.payment.Payment;
import com.j1p3ter.orderserver.domain.payment.PaymentStatus;
import com.j1p3ter.orderserver.implementation.PaymentRequester;
import com.j1p3ter.orderserver.implementation.PaymentSaver;
import com.j1p3ter.orderserver.infrastructure.kafka.EventSerializer;
import com.j1p3ter.orderserver.infrastructure.kafka.event.RollbackStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService {
    private static final Logger logger = LoggerFactory.getLogger(TossPaymentService.class);
    private final PaymentSaver paymentSaver;
    private final PaymentRequester paymentRequester;
    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public PaymentCreateResponseDto requestTossPayment(TossPaymentCreateRequestDto request) {
        logger.debug("Received TossPaymentCreateRequestDto: {}", request);


        // 결제 생성
        Payment payment = paymentSaver.createTossPayment(request.getOrderId(), request.getPaymentKey(), request.getAmount());

        // 추가적으로 결제 상태(newStatus)를 업데이트하거나 저장할 로직을 여기에 추가할 수 있습니다.
        if ("PAYMENT_SUCCESS".equals(request.getNewStatus())) {
            orderService.updateOrderStatus(request.getOrderId(), OrderState.ORDER_COMPLETED, request.getUserId());
        } else if ("PAYMENT_FAILED".equals(request.getNewStatus())) {
            // 결제 실패 시 추가 로직
            OrderResponseDto order = orderService.getOrder(request.getOrderId());
            RollbackStockEvent event = new RollbackStockEvent(0, request.getOrderId(), order.getOrderDetails().get(0).getProductId(), order.getOrderDetails().get(0).getQuantity());

            // Kafka 메시지 생성
            Message<String> kafkaMessage = MessageBuilder
                    .withPayload(EventSerializer.serialize(event))  // 이벤트 직렬화
                    .setHeader(KafkaHeaders.TOPIC, "rollback-stock")  // 토픽 설정
                    .setHeader("X-USER-ID", request.getUserId())                 // 사용자 ID 헤더 설정
                    .build();

            // Kafka 메시지 전송
            kafkaTemplate.send(kafkaMessage);
            orderService.updateOrderStatus(request.getOrderId(), OrderState.PAYMENT_FAILED, request.getUserId());

        }

        return new PaymentCreateResponseDto(payment.getStatus().equals(PaymentStatus.APPROVED), payment.getPaymentId());
    }
}
