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
public class LogEvent {
    private String operation;
    private String description;
    private String serviceName;
    private String methodName;
    private String requestData;
    private String responseData;
    private String errorMessage;
    private String status; // SUCCESS, ERROR
    private String userId;
    private LocalDateTime timestamp;
    private Long executionTime;
    private String ipAddress;
    private String userAgent;
}
