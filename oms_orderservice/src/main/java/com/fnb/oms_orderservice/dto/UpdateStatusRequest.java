package com.fnb.oms_orderservice.dto;

import com.fnb.oms_orderservice.entity.OrderStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private OrderStatus status;
}
