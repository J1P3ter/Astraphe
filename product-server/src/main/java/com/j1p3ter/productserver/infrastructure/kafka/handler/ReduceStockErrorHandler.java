package com.j1p3ter.productserver.infrastructure.kafka.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "ReduceStockErrorHandler")
@RequiredArgsConstructor
public class ReduceStockErrorHandler implements KafkaListenerErrorHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String REDUCE_STOCK_FAIL_TOPIC = "reduce-stock-fail";

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
        return null;
    }

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {
        log.error("kafkaMessage = [" + message.getPayload() + "], errorMessage = [" + exception.getMessage() + "]");

        kafkaTemplate.send(REDUCE_STOCK_FAIL_TOPIC, message.getPayload());
        return null;
    }
}
