package com.feritbilgi.order_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse { //To send to the customer-service
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
}
