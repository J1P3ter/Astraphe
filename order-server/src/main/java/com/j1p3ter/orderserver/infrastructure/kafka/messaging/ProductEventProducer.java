package com.j1p3ter.orderserver.infrastructure.kafka.messaging;

import com.j1p3ter.orderserver.infrastructure.kafka.event.ReduceStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "ProductEventProducer in OrderServer")
public class ProductEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, List<ReduceStockEvent> dto) {
        kafkaTemplate.send(topic, dto);
    }
}
