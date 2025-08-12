package com.feritbilgi.order_service.dto;


import com.feritbilgi.order_service.model.OrderLineItems;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Data  // We add it to have getter/setter and default borderplate  methods
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long customerId; // Müşteri ID'si eklendi
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
