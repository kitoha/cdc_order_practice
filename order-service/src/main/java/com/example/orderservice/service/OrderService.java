package com.example.orderservice.service;

import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final Random random = new Random();
    
    /**
     * 주문 생성
     * ⭐ 중요: 이벤트 발행 코드가 없습니다!
     * CDC(Debezium)가 자동으로 변경사항을 감지하고 Kafka로 발행합니다.
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerName());
        
        String orderNumber = generateOrderNumber();
        
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customerName(request.getCustomerName())
                .totalAmount(request.getTotalAmount())
                .status(OrderStatus.PENDING)
                .notes(request.getNotes())
                .build();
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {}", savedOrder.getOrderNumber());
        
        return toResponse(savedOrder);
    }
    
    /**
     * 주문 상태 변경
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order {} to status: {}", orderId, newStatus);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
        
        // 상태 전환 로직
        switch (newStatus) {
            case APPROVED -> order.approve();
            case SHIPPED -> order.ship();
            case DELIVERED -> order.deliver();
            case CANCELLED -> order.cancel();
            case PENDING -> throw new IllegalStateException("Cannot change status back to PENDING");
        }
        
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully: {} -> {}", 
                updatedOrder.getOrderNumber(), updatedOrder.getStatus());
        
        // 이 업데이트도 CDC가 자동으로 감지합니다!
        
        return toResponse(updatedOrder);
    }
    
    /**
     * 주문 조회
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
        return toResponse(order);
    }
    
    /**
     * 주문번호로 조회
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NoSuchElementException("Order not found with number: " + orderNumber));
        return toResponse(order);
    }
    
    /**
     * 전체 주문 조회
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 상태별 주문 조회
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 주문번호 생성
     */
    private String generateOrderNumber() {
        long timestamp = System.currentTimeMillis();
        int randomNum = random.nextInt(9000) + 1000;
        return String.format("ORD-%d-%d", timestamp, randomNum);
    }
    
    /**
     * Entity to Response 변환
     */
    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomerName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
