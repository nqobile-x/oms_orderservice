package com.fnb.oms_orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private LocalDateTime orderDate;
    private String status;
    private boolean deliveryNeeded;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;
}
