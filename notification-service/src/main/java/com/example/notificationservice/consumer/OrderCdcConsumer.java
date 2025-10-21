package com.example.notificationservice.consumer;

import com.example.notificationservice.model.*;
import com.example.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Debezium CDC 이벤트를 소비하는 Kafka Consumer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCdcConsumer {
    
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(
        topics = "${cdc.topics.orders}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeOrderChanges(String message) {
        try {
            log.debug("Received CDC message: {}", message);

            DebeziumEvent cdcEvent = objectMapper.readValue(message, DebeziumEvent.class);
            
            log.info("Processing CDC event - Operation: {}, Table: {}, Timestamp: {}",
                    cdcEvent.getOp(),
                    cdcEvent.getSource().getTable(),
                    cdcEvent.getTimestamp());

            OrderEvent orderEvent = convertToBusinessEvent(cdcEvent);
            if (orderEvent != null) {
                notificationService.processOrderEvent(orderEvent);
            }
            
        } catch (Exception e) {
            log.error("Failed to process CDC message: {}", message, e);
        }
    }
    
    /**
     * Debezium CDC 이벤트를 비즈니스 이벤트로 변환
     */
    private OrderEvent convertToBusinessEvent(DebeziumEvent cdcEvent) {
        if (cdcEvent.isCreate()) {
            // CREATE: after 데이터 사용
            OrderData order = cdcEvent.getAfter();
            if (order == null) return null;
            
            log.info("Order created: {}", order.getOrderNumber());
            return OrderCreatedEvent.builder()
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .customerName(order.getCustomerName())
                    .status(order.getStatus())
                    .totalAmount(order.getTotalAmount())
                    .build();
            
        } else if (cdcEvent.isUpdate()) {
            // UPDATE: before와 after 비교
            OrderData before = cdcEvent.getBefore();
            OrderData after = cdcEvent.getAfter();
            if (before == null || after == null) return null;
            
            // 상태 변경인 경우만 처리
            if (!before.getStatus().equals(after.getStatus())) {
                log.info("Order status changed: {} {} -> {}",
                        after.getOrderNumber(),
                        before.getStatus(),
                        after.getStatus());
                return OrderStatusChangedEvent.builder()
                        .orderId(after.getId())
                        .orderNumber(after.getOrderNumber())
                        .customerName(after.getCustomerName())
                        .status(after.getStatus())
                        .previousStatus(before.getStatus())
                        .build();
            } else {
                log.debug("Order updated but status unchanged: {}", after.getOrderNumber());
                return null;
            }
            
        } else if (cdcEvent.isDelete()) {
            // DELETE: before 데이터 사용
            OrderData order = cdcEvent.getBefore();
            if (order == null) return null;
            
            log.info("Order deleted: {}", order.getOrderNumber());
            return OrderDeletedEvent.builder()
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .customerName(order.getCustomerName())
                    .status(order.getStatus())
                    .build();
            
        } else if (cdcEvent.isSnapshot()) {
            // SNAPSHOT: 초기 스냅샷 - 보통 무시
            log.debug("Snapshot event - skipping");
            return null;
            
        } else {
            log.warn("Unknown operation type: {}", cdcEvent.getOp());
            return null;
        }
    }
}
