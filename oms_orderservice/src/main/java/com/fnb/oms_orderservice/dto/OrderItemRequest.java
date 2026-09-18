package com.fnb.oms_orderservice.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long itemId;
    private int quantity;
}
