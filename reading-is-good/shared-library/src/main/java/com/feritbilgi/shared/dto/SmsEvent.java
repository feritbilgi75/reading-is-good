package com.feritbilgi.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsEvent {
    private String phoneNumber;
    private String message;
    private String template;
    private String serviceName;
    private String operation;
    private LocalDateTime timestamp;
    private String status; // SENT, FAILED
}
