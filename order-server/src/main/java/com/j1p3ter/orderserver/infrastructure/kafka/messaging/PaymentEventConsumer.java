package com.j1p3ter.orderserver.infrastructure.kafka.messaging;

import com.j1p3ter.common.auditing.AuditorAwareImpl;
import com.j1p3ter.orderserver.application.service.OrderService;
import com.j1p3ter.orderserver.domain.order.OrderState;
import com.j1p3ter.orderserver.infrastructure.kafka.EventSerializer;
import com.j1p3ter.orderserver.infrastructure.kafka.event.ReduceStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "PaymentEventConsumer")
public class PaymentEventConsumer {

    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment-success", groupId = "order-group")
    public void handlePaymentSuccess(ConsumerRecord<?, String> record) {
        log.info("Payment Success Kafka Headers: [{}], Kafka Message: [{}]", record.headers().toString(), record.value());

        String xUserId = record.headers().lastHeader("X-USER-ID") != null ?
                new String(record.headers().lastHeader("X-USER-ID").value()) : "-1";

        AuditorAwareImpl.setAuditor(xUserId);

        ReduceStockEvent event = EventSerializer.deserialize(record.value(), ReduceStockEvent.class);
        try {
            orderService.updateOrderStatus(event.getOrderId(), OrderState.ORDER_COMPLETED, Long.parseLong(xUserId));
        } catch (Exception e) {
            log.error("Error handling payment success: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-failed", groupId = "order-group")
    public void handlePaymentFailed(ConsumerRecord<?, String> record) {
        log.info("Payment Failed Kafka Headers: [{}], Kafka Message: [{}]", record.headers().toString(), record.value());

        String xUserId = record.headers().lastHeader("X-USER-ID") != null ?
                new String(record.headers().lastHeader("X-USER-ID").value()) : "-1";

        AuditorAwareImpl.setAuditor(xUserId);

        ReduceStockEvent event = EventSerializer.deserialize(record.value(), ReduceStockEvent.class);
        try {
            orderService.updateOrderStatus(event.getOrderId(), OrderState.PAYMENT_FAILED, Long.parseLong(xUserId));
        } catch (Exception e) {
            log.error("Error handling payment failed: {}", e.getMessage());
        }
    }

}
