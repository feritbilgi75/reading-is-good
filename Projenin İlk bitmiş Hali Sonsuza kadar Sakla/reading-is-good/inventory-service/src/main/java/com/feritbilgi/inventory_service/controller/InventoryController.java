package com.feritbilgi.inventory_service.controller;

import com.feritbilgi.inventory_service.model.Inventory;
import com.feritbilgi.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Inventory> getAllInventory() {
        log.info("Getting all inventory");
        return inventoryService.getAllInventory();
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Inventory> getInventoryBySkuCodes(@RequestParam List<String> skuCode) {
        log.info("Getting inventory for skuCodes: {}", skuCode);
        return inventoryService.getInventoryBySkuCodes(skuCode);
    }

    @GetMapping("/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public Inventory getInventoryBySkuCode(@PathVariable String skuCode) {
        log.info("Getting inventory for skuCode: {}", skuCode);
        return inventoryService.getInventoryBySkuCode(skuCode);
    }

    @GetMapping("/{skuCode}/check")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable String skuCode, @RequestParam int quantity) {
        log.info("Checking stock for skuCode: {}, quantity: {}", skuCode, quantity);
        return inventoryService.isInStock(skuCode, quantity);
    }

    @PutMapping("/{skuCode}")
    @ResponseStatus(HttpStatus.OK)
    public String updateStock(@PathVariable String skuCode, @RequestParam int quantity) {
        log.info("Updating stock for skuCode: {}, quantity to reduce: {}", skuCode, quantity);
        inventoryService.updateStock(skuCode, quantity);
        return "Stock updated successfully";
    }
} 