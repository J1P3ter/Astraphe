package com.j1p3ter.productserver.presentation;

import com.j1p3ter.productserver.infrastructure.kafka.EventSerializer;
import com.j1p3ter.productserver.infrastructure.kafka.event.ReduceStockEvent;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
@Tag(name = "Kafka", description = "API for Kafka Test")
@Slf4j(topic = "Kafka Test Controller")
public class KafkaTestController {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping
    public String kafkaTest(
            @RequestHeader(name = "X-USER-ID") Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity
    ){
        String topic = "reduce-stock";
        ReduceStockEvent event = new ReduceStockEvent(1L, productId, quantity);
        Message<String> kafkaMessage = MessageBuilder
                .withPayload(EventSerializer.serialize(event))
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader("X-USER-ID", userId)
                .build();
        kafkaTemplate.send(kafkaMessage);
        return "Message is sent";
    }
}
