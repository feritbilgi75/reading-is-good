package com.feritbilgi.customer_service.service;

import com.feritbilgi.customer_service.dto.AuthRequest;
import com.feritbilgi.customer_service.dto.AuthResponse;
import com.feritbilgi.customer_service.dto.CustomerRequest;
import com.feritbilgi.customer_service.dto.CustomerResponse;
import com.feritbilgi.customer_service.model.Customer;
import com.feritbilgi.customer_service.repository.CustomerRepository;
import com.feritbilgi.shared.annotation.LogOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final WebClient.Builder webClientBuilder;

    @LogOperation(operation = "CUSTOMER_CREATED", description = "Yeni müşteri oluşturuldu")
    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        log.info("Creating customer: {}", customerRequest);
        
        Customer customer = Customer.builder()
                .firstName(customerRequest.getFirstName())
                .lastName(customerRequest.getLastName())
                .createdAt(LocalDateTime.now())
                .build();
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created with id: {}", savedCustomer.getId());
        
        return mapToCustomerResponse(savedCustomer);
    }

    @LogOperation(operation = "CUSTOMER_RETRIEVED", description = "Müşteri bilgileri getirildi")
    public CustomerResponse getCustomerById(Long id) {
        log.info("Getting customer by id: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        return mapToCustomerResponse(customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        log.info("Getting all customers");
        
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    @LogOperation(operation = "CUSTOMER_UPDATED", description = "Müşteri bilgileri güncellendi")
    public CustomerResponse updateCustomer(Long id, CustomerRequest customerRequest) {
        log.info("Updating customer with id: {}", id);
        
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        existingCustomer.setFirstName(customerRequest.getFirstName());
        existingCustomer.setLastName(customerRequest.getLastName());
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        log.info("Customer updated with id: {}", updatedCustomer.getId());
        
        return mapToCustomerResponse(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);
        
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        
        customerRepository.deleteById(id);
        log.info("Customer deleted with id: {}", id);
    }

    @LogOperation(operation = "CUSTOMER_REGISTERED", description = "Müşteri kaydı tamamlandı", sendSms = true, smsTemplate = "REGISTRATION_CONFIRMATION")
    public AuthResponse registerCustomer(CustomerRequest customerRequest) {
        log.info("Registering customer: {}", customerRequest.getEmail());
        
        // Check if email already exists
        if (customerRepository.findByEmail(customerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Customer with email " + customerRequest.getEmail() + " already exists");
        }
        
        // Create customer in database
        Customer customer = Customer.builder()
                .firstName(customerRequest.getFirstName())
                .lastName(customerRequest.getLastName())
                .email(customerRequest.getEmail())
                .password(customerRequest.getPassword()) // In production, this should be encrypted
                .createdAt(LocalDateTime.now())
                .build();
        
        Customer savedCustomer = customerRepository.save(customer);
        
        // TODO: Keycloak integration temporarily disabled for testing
        // Create user in Keycloak
        // try {
        //     String keycloakId = createKeycloakUser(customerRequest);
        //     savedCustomer.setKeycloakId(keycloakId);
        //     customerRepository.save(savedCustomer);
        //     log.info("Keycloak user created successfully with ID: {}", keycloakId);
        // } catch (Exception e) {
        //     log.warn("Failed to create Keycloak user, but customer saved locally: {}", e.getMessage());
        //     // Continue without Keycloak integration for now
        // }
        
        log.info("Customer registered successfully with id: {}", savedCustomer.getId());
        
        return AuthResponse.builder()
                .message("Customer registered successfully")
                .customer(mapToCustomerResponse(savedCustomer))
                .build();
    }

    public AuthResponse loginCustomer(AuthRequest authRequest) {
        log.info("Customer login attempt: {}", authRequest.getEmail());
        
        // Find customer by email
        Customer customer = customerRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + authRequest.getEmail()));
        
        // Validate password (in production, use proper password hashing)
        if (!customer.getPassword().equals(authRequest.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        // Get JWT token from Keycloak
        String token;
        try {
            token = getKeycloakToken(authRequest);
            log.info("Keycloak token obtained successfully: {}", token.substring(0, Math.min(50, token.length())) + "...");
        } catch (Exception e) {
            log.error("Failed to get Keycloak token: {}", e.getMessage(), e);
            token = "dummy-token-" + customer.getId(); // Fallback token
        }
        
        log.info("Customer logged in successfully: {}", customer.getEmail());
        
        return AuthResponse.builder()
                .token(token)
                .message("Login successful")
                .customer(mapToCustomerResponse(customer))
                .build();
    }

    private String createKeycloakUser(CustomerRequest customerRequest) {
        try {
            // First, get admin token
            String adminToken = getAdminToken();
            
            // Check if user already exists
            try {
                String existingUserId = getUserIdByEmail(adminToken, customerRequest.getEmail());
                if (existingUserId != null) {
                    log.info("User already exists in Keycloak with ID: {}", existingUserId);
                    return existingUserId;
                }
            } catch (Exception ex) {
                log.info("User does not exist in Keycloak, will create new user");
            }
            
            // Create CUSTOMER role if it doesn't exist
            createRoleIfNotExists(adminToken, "CUSTOMER");
            
            // Create user in Keycloak
            Map<String, Object> userRepresentation = Map.of(
                "username", customerRequest.getEmail(),
                "email", customerRequest.getEmail(),
                "firstName", customerRequest.getFirstName(),
                "lastName", customerRequest.getLastName(),
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(Map.of(
                    "type", "password",
                    "value", customerRequest.getPassword(),
                    "temporary", false
                ))
            );
            
            String response = webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8181/admin/realms/spring-boot-microservices-realm/users")
                    .header("Authorization", "Bearer " + adminToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(userRepresentation)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            // Get user ID from response
            String userId = getUserIdByEmail(adminToken, customerRequest.getEmail());
            
            // Assign CUSTOMER role to the user
            assignRoleToUser(adminToken, userId, "CUSTOMER");
            
            return userId;
            
        } catch (Exception e) {
            log.error("Error creating Keycloak user: {}", e.getMessage());
            // If user already exists (409 Conflict), try to get the existing user ID
            if (e.getMessage().contains("409")) {
                try {
                    String adminToken = getAdminToken();
                    String existingUserId = getUserIdByEmail(adminToken, customerRequest.getEmail());
                    if (existingUserId != null) {
                        log.info("User already exists in Keycloak, using existing ID: {}", existingUserId);
                        return existingUserId;
                    }
                } catch (Exception ex) {
                    log.error("Failed to get existing user ID: {}", ex.getMessage());
                }
            }
            throw new RuntimeException("Failed to create user in Keycloak");
        }
    }

    private String getKeycloakToken(AuthRequest authRequest) {
        try {
            // Use simple HTTP client approach
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            
            String formData = String.format("grant_type=password&client_id=spring-cloud-client&client_secret=0UpbF6VzcxG8ROOBl47SFaSYDjse5UUt&username=%s&password=%s",
                    authRequest.getEmail(), authRequest.getPassword());
            
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8181/realms/spring-boot-microservices-realm/protocol/openid-connect/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(formData))
                    .build();
            
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            
            String responseBody = response.body();
            log.debug("Keycloak token response: {}", responseBody);
            
            // Parse response to get access_token
            if (responseBody != null && responseBody.contains("access_token")) {
                int start = responseBody.indexOf("\"access_token\":\"") + 16;
                int end = responseBody.indexOf("\"", start);
                return responseBody.substring(start, end);
            }
            
            throw new RuntimeException("Failed to get token from Keycloak - no access_token in response");
            
        } catch (Exception e) {
            log.error("Error getting Keycloak token: {}", e.getMessage());
            throw new RuntimeException("Failed to authenticate with Keycloak: " + e.getMessage());
        }
    }

    private String getAdminToken() {
        try {
            String response = webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8181/realms/master/protocol/openid-connect/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("grant_type=password&client_id=admin-cli&username=admin&password=admin")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            if (response != null && response.contains("access_token")) {
                int start = response.indexOf("\"access_token\":\"") + 16;
                int end = response.indexOf("\"", start);
                return response.substring(start, end);
            }
            
            throw new RuntimeException("Failed to get admin token");
            
        } catch (Exception e) {
            log.error("Error getting admin token: {}", e.getMessage());
            throw new RuntimeException("Failed to authenticate admin with Keycloak");
        }
    }

    private String getUserIdByEmail(String adminToken, String email) {
        try {
            String response = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8181/admin/realms/spring-boot-microservices-realm/users?email=" + email)
                    .header("Authorization", "Bearer " + adminToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            // Parse response to get user ID
            // This is a simplified version - in production, use proper JSON parsing
            if (response != null && response.contains("\"id\":")) {
                int start = response.indexOf("\"id\":\"") + 6;
                int end = response.indexOf("\"", start);
                return response.substring(start, end);
            }
            
            // User not found
            return null;
            
        } catch (Exception e) {
            log.error("Error getting user ID: {}", e.getMessage());
            return null;
        }
    }

    private void createRoleIfNotExists(String adminToken, String roleName) {
        try {
            // Check if role exists
            webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8181/admin/realms/spring-boot-microservices-realm/roles/" + roleName)
                    .header("Authorization", "Bearer " + adminToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            log.info("Role {} already exists", roleName);
            
        } catch (Exception e) {
            // Role doesn't exist, create it
            log.info("Role {} doesn't exist, creating it", roleName);
            
            Map<String, Object> roleRepresentation = Map.of(
                "name", roleName,
                "description", roleName + " role for microservices"
            );
            
            webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8181/admin/realms/spring-boot-microservices-realm/roles")
                    .header("Authorization", "Bearer " + adminToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(roleRepresentation)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            log.info("Successfully created role {}", roleName);
        }
    }

    private void assignRoleToUser(String adminToken, String userId, String roleName) {
        try {
            // First, get the role ID
            String roleResponse = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8181/admin/realms/spring-boot-microservices-realm/roles/" + roleName)
                    .header("Authorization", "Bearer " + adminToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            // Parse role ID from response
            if (roleResponse != null && roleResponse.contains("\"id\":")) {
                int start = roleResponse.indexOf("\"id\":\"") + 6;
                int end = roleResponse.indexOf("\"", start);
                String roleId = roleResponse.substring(start, end);
                
                // Create role representation
                Map<String, Object> roleRepresentation = Map.of(
                    "id", roleId,
                    "name", roleName
                );
                
                // Assign role to user
                webClientBuilder.build()
                        .post()
                        .uri("http://localhost:8181/admin/realms/spring-boot-microservices-realm/users/" + userId + "/role-mappings/realm")
                        .header("Authorization", "Bearer " + adminToken)
                        .header("Content-Type", "application/json")
                        .bodyValue(List.of(roleRepresentation))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                
                log.info("Successfully assigned role {} to user {}", roleName, userId);
            }
            
        } catch (Exception e) {
            log.error("Error assigning role {} to user {}: {}", roleName, userId, e.getMessage());
            // Don't throw exception, just log the error
        }
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .createdAt(customer.getCreatedAt())
                .build();
    }
} 