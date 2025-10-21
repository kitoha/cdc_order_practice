package com.example.orderservice.controller;

import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.dto.ApiResponse;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UpdateOrderStatusRequest;
import com.example.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * 주문 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("Received create order request: {}", request);
        
        OrderResponse order = orderService.createOrder(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", order));
    }
    
    /**
     * 주문 상태 변경
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("Received update status request for order {}: {}", orderId, request.getStatus());
        
        OrderResponse order = orderService.updateOrderStatus(orderId, request.getStatus());
        
        return ResponseEntity.ok(
                ApiResponse.success("Order status updated successfully", order));
    }
    
    /**
     * 주문 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrder(orderId);
        
        return ResponseEntity.ok(
                ApiResponse.success("Order retrieved successfully", order));
    }
    
    /**
     * 주문번호로 조회
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(
            @PathVariable String orderNumber) {
        OrderResponse order = orderService.getOrderByOrderNumber(orderNumber);
        
        return ResponseEntity.ok(
                ApiResponse.success("Order retrieved successfully", order));
    }
    
    /**
     * 전체 주문 조회 / 상태별 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @RequestParam(required = false) OrderStatus status) {
        List<OrderResponse> orders = status != null
                ? orderService.getOrdersByStatus(status)
                : orderService.getAllOrders();
        
        return ResponseEntity.ok(
                ApiResponse.success("Orders retrieved successfully", orders));
    }
    
    /**
     * 예외 처리 - Not Found
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoSuchElementException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 예외 처리 - Illegal State
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException e) {
        log.warn("Illegal state: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 예외 처리 - Generic Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred"));
    }
}
