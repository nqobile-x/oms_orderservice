package com.fnb.oms_orderservice.repository;

import com.fnb.oms_orderservice.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
}
