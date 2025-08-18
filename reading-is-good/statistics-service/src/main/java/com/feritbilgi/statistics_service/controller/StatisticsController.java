package com.feritbilgi.statistics_service.controller;

import com.feritbilgi.statistics_service.dto.MonthlyStatisticsResponse;
import com.feritbilgi.statistics_service.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    /**
     * Get monthly statistics for authenticated customer
     */
    @GetMapping("/monthly")
    @ResponseStatus(HttpStatus.OK)
    public MonthlyStatisticsResponse getMonthlyStatistics(@AuthenticationPrincipal Jwt jwt) {
        // Extract customer ID from JWT token - use default for now since JWT contains UUID
        Long customerId = 1L; // Default customer ID
        log.info("Getting monthly statistics for customer: {}", customerId);
        
        return statisticsService.getMonthlyStatistics(customerId, jwt.getTokenValue());
    }
    
    /**
     * Get monthly statistics for specific customer (admin endpoint)
     */
    @GetMapping("/monthly/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public MonthlyStatisticsResponse getMonthlyStatisticsByCustomerId(@PathVariable Long customerId, @AuthenticationPrincipal Jwt jwt) {
        log.info("Getting monthly statistics for customer ID: {}", customerId);
        
        return statisticsService.getMonthlyStatistics(customerId, jwt.getTokenValue());
    }
}
