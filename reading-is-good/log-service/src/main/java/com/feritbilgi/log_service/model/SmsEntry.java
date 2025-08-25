package com.feritbilgi.log_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String phoneNumber;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    private String template;
    private String serviceName;
    private String operation;
    private LocalDateTime timestamp;
    private String status;
}
