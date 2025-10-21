package com.example.notificationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * CDC 이벤트의 주문 데이터
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderData {
    
    private Long id;
    
    @JsonProperty("order_number")
    private String orderNumber;
    
    @JsonProperty("customer_name")
    private String customerName;
    
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    
    private String status;
    
    private String notes;
    
    @JsonProperty("created_at")
    private Long createdAt;
    
    @JsonProperty("updated_at")
    private Long updatedAt;
}
