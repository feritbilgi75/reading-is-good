package com.feritbilgi.customer_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    
    @Column(unique = true)
    private String email;
    
    private String password;
    private String keycloakId; // Keycloak'tan gelen user ID
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
