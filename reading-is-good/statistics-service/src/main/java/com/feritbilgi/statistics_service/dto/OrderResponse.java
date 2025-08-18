package com.feritbilgi.statistics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private List<OrderLineItemResponse> orderLineItemsList;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}
