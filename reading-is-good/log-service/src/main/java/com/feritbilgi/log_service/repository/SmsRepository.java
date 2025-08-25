package com.feritbilgi.log_service.repository;

import com.feritbilgi.log_service.model.SmsEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmsRepository extends JpaRepository<SmsEntry, Long> {
    
    List<SmsEntry> findByServiceNameOrderByTimestampDesc(String serviceName);
    
    List<SmsEntry> findByStatusOrderByTimestampDesc(String status);
    
    List<SmsEntry> findByPhoneNumberOrderByTimestampDesc(String phoneNumber);
}
