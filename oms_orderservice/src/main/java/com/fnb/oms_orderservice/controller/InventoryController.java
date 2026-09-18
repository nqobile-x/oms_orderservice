package com.fnb.oms_orderservice.controller;

import com.fnb.oms_orderservice.dto.InventoryItemRequest;
import com.fnb.oms_orderservice.dto.InventoryItemResponse;
import com.fnb.oms_orderservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
//required args constructor is used to generate a constructor with required arguments (final fields) for the class. It is part of the Lombok library and helps reduce boilerplate code by automatically generating constructors, getters, setters, and other methods based on annotations.
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryItemResponse> addItem(@RequestBody InventoryItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.addItem(request));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<InventoryItemResponse> getItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(inventoryService.getItem(itemId));
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemResponse>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @PutMapping("/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryItemResponse> updateItem(@PathVariable Long itemId,
                                                             @RequestBody InventoryItemRequest request) {
        return ResponseEntity.ok(inventoryService.updateItem(itemId, request));
    }

    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        inventoryService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
