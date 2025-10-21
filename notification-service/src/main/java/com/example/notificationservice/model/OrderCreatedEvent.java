package com.example.notificationservice.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 주문 생성 이벤트
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderCreatedEvent extends OrderEvent {
    
    private BigDecimal totalAmount;
    
    @Builder
    public OrderCreatedEvent(Long orderId, String orderNumber, String customerName, 
                            String status, BigDecimal totalAmount) {
        super(orderId, orderNumber, customerName, status);
        this.totalAmount = totalAmount;
    }
}
