
package com.techie.microservices.inventory.service;

import com.techie.microservices.inventory.exception.ProductNotFoundException;
import com.techie.microservices.inventory.exception.ProductOutOfStockException;
import com.techie.microservices.inventory.model.Inventory;
import com.techie.microservices.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public boolean isInStock(String skuCode, Integer quantity) {
        log.info("Checking stock for product {} and quantity {}", skuCode, quantity);

        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseGet(() -> {
                    log.warn("❌ Product unavalaible with code : {}", skuCode);
                    throw new ProductNotFoundException(skuCode);
                });


        // 2️⃣ Check if enough stock is available
        if (inventory.getQuantity() < quantity) {
            log.info("❌ Product '{}' unavailable with stock '{}'", skuCode, inventory.getQuantity());
            throw new ProductOutOfStockException(skuCode, quantity, inventory.getQuantity());
        }

        log.info("✅ Product {} is in stock for requested quantity {}", skuCode, quantity);
        return true;
    }
}
