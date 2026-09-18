package com.fnb.oms_orderservice.service;

import com.fnb.oms_orderservice.dto.CreateOrderRequest;
import com.fnb.oms_orderservice.dto.OrderResponse;
import com.fnb.oms_orderservice.dto.UpdateStatusRequest;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long customerId, CreateOrderRequest request);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersByCustomer(Long customerId);
    OrderResponse updateStatus(Long orderId, UpdateStatusRequest request);
}
