package com.fnb.oms_orderservice.service.serviceImpl;

import com.fnb.oms_orderservice.dto.InventoryItemRequest;
import com.fnb.oms_orderservice.dto.InventoryItemResponse;
import com.fnb.oms_orderservice.entity.InventoryItem;
import com.fnb.oms_orderservice.repository.InventoryItemRepository;
import com.fnb.oms_orderservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    @Override
    public InventoryItemResponse addItem(InventoryItemRequest request) {
        InventoryItem item = InventoryItem.builder()
                .itemName(request.getItemName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();
        return toResponse(inventoryItemRepository.save(item));
    }

    @Override
    public InventoryItemResponse getItem(Long itemId) {
        return toResponse(findById(itemId));
    }

    @Override
    public List<InventoryItemResponse> getAllItems() {
        return inventoryItemRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public InventoryItemResponse updateItem(Long itemId, InventoryItemRequest request) {
        InventoryItem item = findById(itemId);
        item.setItemName(request.getItemName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setStockQuantity(request.getStockQuantity());
        return toResponse(inventoryItemRepository.save(item));
    }

    @Override
    public void deleteItem(Long itemId) {
        inventoryItemRepository.delete(findById(itemId));
    }

    private InventoryItem findById(Long itemId) {
        return inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
    }

    private InventoryItemResponse toResponse(InventoryItem item) {
        return InventoryItemResponse.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stockQuantity(item.getStockQuantity())
                .build();
    }
}
