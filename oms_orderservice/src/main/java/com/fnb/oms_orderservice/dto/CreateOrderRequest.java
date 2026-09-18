package com.fnb.oms_orderservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private boolean deliveryNeeded;
    private List<OrderItemRequest> items;
}
