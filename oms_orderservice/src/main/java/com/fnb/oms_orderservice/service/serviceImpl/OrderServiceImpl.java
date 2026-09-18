package com.fnb.oms_orderservice.service.serviceImpl;

import com.fnb.oms_orderservice.dto.*;
import com.fnb.oms_orderservice.entity.*;
import com.fnb.oms_orderservice.repository.InventoryItemRepository;
import com.fnb.oms_orderservice.repository.OrderRepository;
import com.fnb.oms_orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(Long customerId, CreateOrderRequest request) {
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            InventoryItem inventoryItem = inventoryItemRepository.findById(itemReq.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemReq.getItemId()));

            if (inventoryItem.getStockQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Insufficient stock for item: " + inventoryItem.getItemName());
            }

            try {
                inventoryItem.setStockQuantity(inventoryItem.getStockQuantity() - itemReq.getQuantity());
                inventoryItemRepository.saveAndFlush(inventoryItem);
            } catch (ObjectOptimisticLockingFailureException e) {
                InventoryItem fresh = inventoryItemRepository.findById(itemReq.getItemId()).orElseThrow();
                if (fresh.getStockQuantity() < itemReq.getQuantity()) {
                    throw new RuntimeException("Item no longer available in requested quantity: " + fresh.getItemName());
                }
                fresh.setStockQuantity(fresh.getStockQuantity() - itemReq.getQuantity());
                inventoryItemRepository.saveAndFlush(fresh);
            }

            BigDecimal unitPrice = inventoryItem.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            orderItems.add(OrderItem.builder()
                    .inventoryItem(inventoryItem)
                    .quantity(itemReq.getQuantity())
                    .unitPriceAtPurchase(unitPrice)
                    .subtotal(subtotal)
                    .build());
        }

        Order order = Order.builder()
                .customerId(customerId)
                .status(OrderStatus.PLACED)
                .deliveryNeeded(request.isDeliveryNeeded())
                .totalAmount(totalAmount)
                .build();

        orderItems.forEach(i -> i.setOrder(order));
        order.setItems(orderItems);

        return toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        return toResponse(findById(orderId));
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, UpdateStatusRequest request) {
        Order order = findById(orderId);
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Order is already DELIVERED and cannot be updated");
        }
        order.setStatus(request.getStatus());
        return toResponse(orderRepository.save(order));
    }

    private Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .orderItemId(i.getOrderItemId())
                        .itemId(i.getInventoryItem().getItemId())
                        .itemName(i.getInventoryItem().getItemName())
                        .quantity(i.getQuantity())
                        .unitPriceAtPurchase(i.getUnitPriceAtPurchase())
                        .subtotal(i.getSubtotal())
                        .build())
                .toList();

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .customerId(order.getCustomerId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .deliveryNeeded(order.isDeliveryNeeded())
                .totalAmount(order.getTotalAmount())
                .items(itemResponses)
                .build();
    }
}
