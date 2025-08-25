package com.feritbilgi.customer_service.controller;

import com.feritbilgi.customer_service.dto.AuthRequest;
import com.feritbilgi.customer_service.dto.AuthResponse;
import com.feritbilgi.customer_service.dto.CustomerRequest;
import com.feritbilgi.customer_service.dto.CustomerResponse;
import com.feritbilgi.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@RequestBody CustomerRequest customerRequest) {
        log.info("Creating customer: {}", customerRequest);
        return customerService.createCustomer(customerRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponse getCustomerById(@PathVariable Long id) {
        log.info("Getting customer by id: {}", id);
        return customerService.getCustomerById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerResponse> getAllCustomers() {
        log.info("Getting all customers");
        return customerService.getAllCustomers();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerResponse updateCustomer(@PathVariable Long id, @RequestBody CustomerRequest customerRequest) {
        log.info("Updating customer with id: {}", id);
        return customerService.updateCustomer(id, customerRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with id: {}", id);
        customerService.deleteCustomer(id);
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerCustomer(@RequestBody CustomerRequest customerRequest) {
        log.info("Customer registration request: {}", customerRequest.getEmail());
        return customerService.registerCustomer(customerRequest);
    }

    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse loginCustomer(@RequestBody AuthRequest authRequest) {
        log.info("Customer login request: {}", authRequest.getEmail());
        return customerService.loginCustomer(authRequest);
    }
}
