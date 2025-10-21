package com.example.notificationservice.service;

import com.example.notificationservice.model.OrderCreatedEvent;
import com.example.notificationservice.model.OrderDeletedEvent;
import com.example.notificationservice.model.OrderEvent;
import com.example.notificationservice.model.OrderStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {
    
    /**
     * 주문 이벤트 처리
     */
    public void processOrderEvent(OrderEvent event) {
        if (event instanceof OrderCreatedEvent) {
            handleOrderCreated((OrderCreatedEvent) event);
        } else if (event instanceof OrderStatusChangedEvent) {
            handleOrderStatusChanged((OrderStatusChangedEvent) event);
        } else if (event instanceof OrderDeletedEvent) {
            handleOrderDeleted((OrderDeletedEvent) event);
        }
    }
    
    /**
     * 주문 생성 처리
     */
    private void handleOrderCreated(OrderCreatedEvent event) {
        log.info("""
                ========================================
                📦 NEW ORDER NOTIFICATION
                ========================================
                Order Number: {}
                Customer: {}
                Amount: {}
                Status: {}
                ----------------------------------------
                Action: Sending confirmation email...
                ========================================
                """,
                event.getOrderNumber(),
                event.getCustomerName(),
                event.getTotalAmount(),
                event.getStatus());

        sendEmail(
            event.getCustomerName() + "@example.com",
            "주문이 접수되었습니다",
            "주문번호 " + event.getOrderNumber() + "가 접수되었습니다."
        );
    }
    
    /**
     * 주문 상태 변경 처리
     */
    private void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("""
                ========================================
                🔄 ORDER STATUS UPDATE NOTIFICATION
                ========================================
                Order Number: {}
                Customer: {}
                Status Change: {} → {}
                ----------------------------------------
                Action: Sending status update...
                ========================================
                """,
                event.getOrderNumber(),
                event.getCustomerName(),
                event.getPreviousStatus(),
                event.getStatus());
        
        String message = switch (event.getStatus()) {
            case "APPROVED" -> "주문이 승인되었습니다.";
            case "SHIPPED" -> "주문 상품이 배송되었습니다.";
            case "DELIVERED" -> "주문 상품이 배송 완료되었습니다.";
            case "CANCELLED" -> "주문이 취소되었습니다.";
            default -> "주문 상태가 변경되었습니다.";
        };
        
        sendEmail(
            event.getCustomerName() + "@example.com",
            "주문 상태 업데이트",
            "주문번호 " + event.getOrderNumber() + ": " + message
        );
        
        // 배송 시작 시 추가 작업
        if ("SHIPPED".equals(event.getStatus())) {
            sendSms(
                "+82-10-1234-5678",
                "[배송시작] " + event.getOrderNumber() + " 상품이 배송되었습니다."
            );
        }
    }
    
    /**
     * 주문 삭제 처리
     */
    private void handleOrderDeleted(OrderDeletedEvent event) {
        log.info("""
                ========================================
                🗑️  ORDER DELETION NOTIFICATION
                ========================================
                Order Number: {}
                Customer: {}
                ----------------------------------------
                Action: Logging deletion event...
                ========================================
                """,
                event.getOrderNumber(),
                event.getCustomerName());
        
        // 주문 삭제는 보통 관리자 작업이므로 내부 로깅
        logToAuditSystem(
            "ORDER_DELETED",
            event.getOrderId(),
            "Order " + event.getOrderNumber() + " was deleted"
        );
    }
    
    /**
     * 이메일 발송 (실제 구현에서는 이메일 서비스 호출)
     */
    private void sendEmail(String to, String subject, String body) {
        log.info("📧 Sending email to {}: {}", to, subject);
        // TODO: 실제 이메일 발송 구현 (SendGrid, AWS SES 등)
    }
    
    /**
     * SMS 발송 (실제 구현에서는 SMS 서비스 호출)
     */
    private void sendSms(String to, String message) {
        log.info("📱 Sending SMS to {}: {}", to, message);
        // TODO: 실제 SMS 발송 구현 (Twilio, AWS SNS 등)
    }
    
    /**
     * 감사 로그 (실제 구현에서는 감사 시스템에 로깅)
     */
    private void logToAuditSystem(String action, Long orderId, String details) {
        log.info("📝 Audit log: [{}] Order {} - {}", action, orderId, details);
        // TODO: 실제 감사 시스템 연동 (Elasticsearch, DataDog 등)
    }
}
