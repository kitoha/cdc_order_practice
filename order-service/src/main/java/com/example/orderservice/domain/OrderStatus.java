package com.example.orderservice.domain;

/**
 * 주문 상태
 */
public enum OrderStatus {
    PENDING,      // 주문 생성됨
    APPROVED,     // 주문 승인됨
    SHIPPED,      // 배송 시작
    DELIVERED,    // 배송 완료
    CANCELLED     // 주문 취소
}
