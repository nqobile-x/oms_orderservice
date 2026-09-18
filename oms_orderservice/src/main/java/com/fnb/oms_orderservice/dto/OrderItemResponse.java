package com.fnb.oms_orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long orderItemId;
    private Long itemId;
    private String itemName;
    private int quantity;
    private BigDecimal unitPriceAtPurchase;
    private BigDecimal subtotal;
}
