package com.feritbilgi.inventory_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_inventory")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sku_code", unique = true)
    private String skuCode;
    
    private Integer quantity;
    
    @Enumerated(EnumType.STRING)
    private InventoryStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void setStatusBeforePersist() {  //Obje oluşturulduğunda @Entity'de otomatik tetikleniyor.
        if (this.status == null) {
            if (this.quantity <= 0) {
                this.status = InventoryStatus.OUT_OF_STOCK;
            } else if (this.quantity < 10) {
                this.status = InventoryStatus.LOW_STOCK;
            } else {
                this.status = InventoryStatus.IN_STOCK;
            }
        }
    }
} 