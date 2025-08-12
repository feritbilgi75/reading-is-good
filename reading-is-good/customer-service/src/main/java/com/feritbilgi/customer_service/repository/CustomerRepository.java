package com.feritbilgi.customer_service.repository;

import com.feritbilgi.customer_service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByFirstNameAndLastName(String firstName, String lastName);
} 