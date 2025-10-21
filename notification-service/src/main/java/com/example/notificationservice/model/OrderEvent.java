package com.example.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 주문 비즈니스 이벤트 (추상 클래스)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class OrderEvent {
    
    private Long orderId;
    private String orderNumber;
    private String customerName;
    private String status;
}
