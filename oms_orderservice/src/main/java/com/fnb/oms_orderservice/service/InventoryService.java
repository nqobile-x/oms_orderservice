package com.fnb.oms_orderservice.service;

import com.fnb.oms_orderservice.dto.InventoryItemRequest;
import com.fnb.oms_orderservice.dto.InventoryItemResponse;

import java.util.List;

public interface InventoryService {
    InventoryItemResponse addItem(InventoryItemRequest request);
    InventoryItemResponse getItem(Long itemId);
    List<InventoryItemResponse> getAllItems();
    InventoryItemResponse updateItem(Long itemId, InventoryItemRequest request);
    void deleteItem(Long itemId);
}
