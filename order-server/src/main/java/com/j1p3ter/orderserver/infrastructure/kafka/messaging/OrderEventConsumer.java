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
@Slf4j(topic = "OrderEventConsumer")
public class OrderEventConsumer {

    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "reduce-stock-success", groupId = "order-group")
    public void handleReduceStockSuccess(ConsumerRecord<?, String> record) {
        log.info("Kafka Headers : [" + record.headers().toString() + "], Kafka Message : [" + record.value() + "]");
        String xUserId = record.headers().lastHeader("X-USER-ID") != null ?
                new String(record.headers().lastHeader("X-USER-ID").value()) : "-1";

        AuditorAwareImpl.setAuditor(xUserId);


        ReduceStockEvent event = EventSerializer.deserialize(record.value(), ReduceStockEvent.class);
        try {
            orderService.updateOrderStatus(event.getOrderId(), OrderState.ORDER_CREATED, Long.parseLong(xUserId));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @KafkaListener(topics = "reduce-stock-failed", groupId = "order-group")
    public void handleReduceStockFailed(ConsumerRecord<?, String> record) {
        log.info("Kafka Headers : [" + record.headers().toString() + "], Kafka Message : [" + record.value() + "]");
        String xUserId = record.headers().lastHeader("X-USER-ID") != null ?
                new String(record.headers().lastHeader("X-USER-ID").value()) : "-1";

        AuditorAwareImpl.setAuditor(xUserId);


        ReduceStockEvent event = EventSerializer.deserialize(record.value(), ReduceStockEvent.class);
        try {
            orderService.updateOrderStatus(event.getOrderId(), OrderState.ORDER_FAILED, Long.parseLong(xUserId));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
