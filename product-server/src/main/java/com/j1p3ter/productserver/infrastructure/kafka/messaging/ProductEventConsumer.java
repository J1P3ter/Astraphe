package com.j1p3ter.productserver.infrastructure.kafka.messaging;

import com.j1p3ter.common.auditing.AuditorAwareImpl;
import com.j1p3ter.productserver.application.ProductService;
import com.j1p3ter.productserver.infrastructure.kafka.EventSerializer;
import com.j1p3ter.productserver.infrastructure.kafka.event.ReduceStockEvent;
import com.j1p3ter.productserver.infrastructure.kafka.event.RollbackStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "ProductEventConsumer")
public class ProductEventConsumer {

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String REDUCE_STOCK_SUCCESS = "reduce-stock-success";
    private static final String ROLLBACK_STOCK_SUCCESS = "rollback-stock-success";

    @KafkaListener(topics = "reduce-stock", groupId = "product-group", errorHandler = "reduceStockErrorHandler")
    public void handleReduceStock(ConsumerRecord<?,String> record){
        log.info("Kafka Headers : [" + record.headers().toString() + "], Kafka Message : [" + record.value() + "]");
        String xUserId = record.headers().lastHeader("X-USER-ID") != null ?
                new String(record.headers().lastHeader("X-USER-ID").value()) : "-1";

        AuditorAwareImpl.setAuditor(xUserId);

        ReduceStockEvent event = EventSerializer.deserialize(record.value(), ReduceStockEvent.class);
        try{
            productService.reduceStock(event.getProductId(), event.getQuantity());
            log.info("Reduce Stock Success");
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }finally {
            AuditorAwareImpl.clear();
        }

        Message<String> kafkaMessage = MessageBuilder
                .withPayload(EventSerializer.serialize(event))
                .setHeader(KafkaHeaders.TOPIC, REDUCE_STOCK_SUCCESS)
                .setHeader("X-USER-ID", xUserId)
                .build();

        kafkaTemplate.send(kafkaMessage);
    }

    @KafkaListener(topics = "rollback-stock", groupId = "product-group", errorHandler = "rollbackStockErrorHandler")
    public void handleRollbackStock(ConsumerRecord<?,String> record){
        log.info("Kafka Headers : [" + record.headers().toString() + "], Kafka Message : [" + record.value() + "]");
        String xUserId = record.headers().lastHeader("X-USER-ID") != null ?
                new String(record.headers().lastHeader("X-USER-ID").value()) : "-1";

        AuditorAwareImpl.setAuditor(xUserId);

        RollbackStockEvent event = EventSerializer.deserialize(record.value(), RollbackStockEvent.class);
        try{
            productService.addStock(event.getProductId(), event.getQuantity());
            log.info("Rollback Stock Success");
        }catch (Exception e){
            throw new KafkaException(e.getMessage());
        }finally {
            AuditorAwareImpl.clear();
        }

        Message<String> kafkaMessage = MessageBuilder
                .withPayload(EventSerializer.serialize(event))
                .setHeader(KafkaHeaders.TOPIC, ROLLBACK_STOCK_SUCCESS)
                .setHeader("X-USER-ID", xUserId)
                .build();

        kafkaTemplate.send(kafkaMessage);
    }

}
