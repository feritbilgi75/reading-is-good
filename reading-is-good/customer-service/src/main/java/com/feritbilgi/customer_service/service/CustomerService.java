package com.feritbilgi.customer_service.service;

import com.feritbilgi.customer_service.dto.CustomerRequest;
import com.feritbilgi.customer_service.dto.CustomerResponse;
import com.feritbilgi.customer_service.model.Customer;
import com.feritbilgi.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

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

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .createdAt(customer.getCreatedAt())
                .build();
    }
} 