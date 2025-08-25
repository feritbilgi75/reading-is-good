package com.feritbilgi.log_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String operation;
    private String description;
    private String serviceName;
    private String methodName;
    
    @Column(columnDefinition = "TEXT")
    private String requestData;
    
    @Column(columnDefinition = "TEXT")
    private String responseData;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    private String status;
    private String userId;
    private LocalDateTime timestamp;
    private Long executionTime;
    private String ipAddress;
    private String userAgent;
}
