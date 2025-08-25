package com.feritbilgi.log_service.repository;

import com.feritbilgi.log_service.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntry, Long> {
    
    List<LogEntry> findByServiceNameOrderByTimestampDesc(String serviceName);
    
    List<LogEntry> findByStatusOrderByTimestampDesc(String status);
    
    List<LogEntry> findByOperationOrderByTimestampDesc(String operation);
    
    @Query("SELECT l FROM LogEntry l WHERE l.timestamp BETWEEN :startDate AND :endDate ORDER BY l.timestamp DESC")
    List<LogEntry> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT l FROM LogEntry l WHERE l.serviceName = :serviceName AND l.timestamp BETWEEN :startDate AND :endDate ORDER BY l.timestamp DESC")
    List<LogEntry> findByServiceNameAndDateRange(@Param("serviceName") String serviceName, 
                                                @Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
}
