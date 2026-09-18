package com.fnb.oms_orderservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryItemRequest {
    private String itemName;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
}
