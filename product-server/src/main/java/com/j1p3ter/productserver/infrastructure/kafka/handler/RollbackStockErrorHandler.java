package com.j1p3ter.productserver.infrastructure.kafka.handler;

import com.j1p3ter.productserver.infrastructure.kafka.EventSerializer;
import com.j1p3ter.productserver.infrastructure.kafka.event.ReduceStockEvent;
import com.j1p3ter.productserver.infrastructure.kafka.event.RollbackStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "RollbackStockErrorHandler")
@RequiredArgsConstructor
public class RollbackStockErrorHandler implements KafkaListenerErrorHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ROLLBACK_STOCK_FAILED = "rollback-stock-failed";

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
        return null;
    }

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {
        ConsumerRecord<String, String> record = (ConsumerRecord<String, String>) message.getPayload();
        log.info("Kafka Headers : [" + record.headers().toString() + "], Kafka Message : [" + record.value() + "]");

        String xUserId = record.headers().lastHeader("X-USER-ID") != null ?
                new String(record.headers().lastHeader("X-USER-ID").value()) : "-1";

        RollbackStockEvent event = EventSerializer.deserialize(record.value(), RollbackStockEvent.class);
        event.increaseRetryCount(); // 재시도 횟수 1 증가

        Message<String> kafkaMessage = MessageBuilder
                .withPayload(EventSerializer.serialize(event))
                .setHeader(KafkaHeaders.TOPIC, ROLLBACK_STOCK_FAILED)
                .setHeader("X-USER-ID", xUserId)
                .build();

        kafkaTemplate.send(kafkaMessage);
        return null;
    }
}
