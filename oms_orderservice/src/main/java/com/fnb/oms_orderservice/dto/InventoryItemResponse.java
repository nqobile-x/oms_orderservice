package com.fnb.oms_orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InventoryItemResponse {
    private Long itemId;
    private String itemName;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
}
