package com.feritbilgi.statistics_service.service;

import com.feritbilgi.statistics_service.dto.MonthlyStatisticsResponse;
import com.feritbilgi.statistics_service.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {
    
    private final WebClient.Builder webClientBuilder;
    
    /**
     * Calculate monthly statistics for a customer
     */
    public MonthlyStatisticsResponse getMonthlyStatistics(Long customerId, String jwtToken) {
        log.info("Calculating monthly statistics for customer: {}", customerId);
        
        // Get all orders for the customer
        List<OrderResponse> orders = getCustomerOrders(customerId, jwtToken);
        
        // Group orders by month and calculate statistics
        return calculateMonthlyStats(orders);
    }
    
    /**
     * Fetch customer orders from order-service
     */
    private List<OrderResponse> getCustomerOrders(Long customerId, String jwtToken) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/order/customer/my-orders")
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .bodyToFlux(OrderResponse.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.error("Error fetching orders from order-service: {}", e.getMessage());
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Calculate monthly statistics from orders
     */
    private MonthlyStatisticsResponse calculateMonthlyStats(List<OrderResponse> orders) {
        if (orders == null || orders.isEmpty()) {
            return MonthlyStatisticsResponse.builder()
                    .month("No Data")
                    .totalOrderCount(0L)
                    .totalBookCount(0L)
                    .totalPurchasedAmount(BigDecimal.ZERO)
                    .build();
        }
        
        // Get current month
        String currentMonth = LocalDateTime.now().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        
        // Calculate totals
        long totalOrderCount = orders.size();
        long totalBookCount = orders.stream()
                .flatMapToInt(order -> order.getOrderLineItemsList().stream()
                        .mapToInt(item -> item.getQuantity()))
                .sum();
        BigDecimal totalAmount = orders.stream()
                .map(OrderResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return MonthlyStatisticsResponse.builder()
                .month(currentMonth)
                .totalOrderCount(totalOrderCount)
                .totalBookCount(totalBookCount)
                .totalPurchasedAmount(totalAmount)
                .build();
    }
}
