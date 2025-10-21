package com.example.notificationservice.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 주문 상태 변경 이벤트
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderStatusChangedEvent extends OrderEvent {
    
    private String previousStatus;
    
    @Builder
    public OrderStatusChangedEvent(Long orderId, String orderNumber, String customerName, 
                                  String status, String previousStatus) {
        super(orderId, orderNumber, customerName, status);
        this.previousStatus = previousStatus;
    }
}
