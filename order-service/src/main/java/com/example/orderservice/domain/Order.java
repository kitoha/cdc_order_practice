package com.example.orderservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;
    
    @Column(nullable = false, length = 100)
    private String customerName;
    
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;
    
    @Column(length = 500)
    private String notes;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 주문 승인
     */
    public void approve() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be approved");
        }
        this.status = OrderStatus.APPROVED;
    }
    
    /**
     * 배송 시작
     */
    public void ship() {
        if (status != OrderStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED orders can be shipped");
        }
        this.status = OrderStatus.SHIPPED;
    }
    
    /**
     * 배송 완료
     */
    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only SHIPPED orders can be delivered");
        }
        this.status = OrderStatus.DELIVERED;
    }
    
    /**
     * 주문 취소
     */
    public void cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel DELIVERED orders");
        }
        this.status = OrderStatus.CANCELLED;
    }
}
