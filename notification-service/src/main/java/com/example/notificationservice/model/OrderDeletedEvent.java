package com.example.notificationservice.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 주문 삭제 이벤트
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDeletedEvent extends OrderEvent {
    
    @Builder
    public OrderDeletedEvent(Long orderId, String orderNumber, String customerName, String status) {
        super(orderId, orderNumber, customerName, status);
    }
}
