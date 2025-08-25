package com.feritbilgi.inventory_service.service;

import com.feritbilgi.inventory_service.model.Inventory;
import com.feritbilgi.inventory_service.model.InventoryStatus;
import com.feritbilgi.inventory_service.repository.InventoryRepository;
import com.feritbilgi.shared.annotation.LogOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @LogOperation(operation = "INVENTORY_RETRIEVED", description = "Tüm envanter bilgileri getirildi")
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    // @SneakyThrows
    public List<Inventory> getInventoryBySkuCodes(List<String> skuCodes) {
        // log.info("Wait started");
        // Thread.sleep(10000); // 10 saniye bekle
        // log.info("Wait ended");
        
        return inventoryRepository.findBySkuCodeIn(skuCodes);
    }

    // @SneakyThrows
    public Inventory getInventoryBySkuCode(String skuCode) {
        // log.info("Wait started");
        // Thread.sleep(10000); // 10 saniye bekle
        // log.info("Wait ended");
        
        return inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("Inventory not found for skuCode: " + skuCode));
    }

    // @SneakyThrows
    public boolean isInStock(String skuCode, int quantity) {
        // log.info("Wait started");
        // Thread.sleep(10000); // 10 saniye bekle
        // log.info("Wait ended");
        
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElse(null);
        return inventory != null && inventory.getQuantity() >= quantity;
    }

    @LogOperation(operation = "INVENTORY_UPDATED", description = "Envanter stoku güncellendi")
    public void updateStock(String skuCode, int quantity) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("Inventory not found for skuCode: " + skuCode));
        
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        // Update status based on quantity
        if (inventory.getQuantity() <= 0) {
            inventory.setStatus(InventoryStatus.OUT_OF_STOCK);
        } else if (inventory.getQuantity() < 10) {
            inventory.setStatus(InventoryStatus.LOW_STOCK);
        } else {
            inventory.setStatus(InventoryStatus.IN_STOCK);
        }
        
        inventoryRepository.save(inventory);
        log.info("Stock updated for skuCode: {}, new quantity: {}", skuCode, inventory.getQuantity());
    }
} 