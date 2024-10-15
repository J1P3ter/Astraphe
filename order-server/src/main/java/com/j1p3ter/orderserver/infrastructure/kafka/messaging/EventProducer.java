package com.j1p3ter.orderserver.infrastructure.kafka.messaging;

import com.j1p3ter.orderserver.infrastructure.kafka.EventSerializer;
import com.j1p3ter.orderserver.infrastructure.kafka.event.ReduceStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "EventProducer in OrderServer")
public class EventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, List<ReduceStockEvent> events, Long userId) {
        // 각 이벤트를 개별적으로 Kafka 메시지로 전송
        events.forEach(event -> {
            // Kafka 메시지 생성
            Message<String> kafkaMessage = MessageBuilder
                    .withPayload(EventSerializer.serialize(event))  // 이벤트를 직렬화
                    .setHeader(KafkaHeaders.TOPIC, topic)           // 토픽 설정
                    .setHeader("X-USER-ID", userId)                 // 사용자 ID 헤더 추가
                    .build();

            // Kafka 메시지 전송
            kafkaTemplate.send(kafkaMessage);
        });
    }
}
