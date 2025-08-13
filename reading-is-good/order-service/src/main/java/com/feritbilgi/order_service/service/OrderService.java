package com.feritbilgi.order_service.service;

import com.feritbilgi.order_service.dto.OrderLineItemsDto;
import com.feritbilgi.order_service.dto.OrderRequest;
import com.feritbilgi.order_service.dto.InventoryResponse;
import com.feritbilgi.order_service.model.Order;
import com.feritbilgi.order_service.model.OrderLineItems;
import com.feritbilgi.order_service.model.OrderStatus;
import com.feritbilgi.order_service.repository.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomerId(orderRequest.getCustomerId()); // Customer ID'yi set et
        
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        
        order.setOrderLineItemsList(orderLineItems);

        // Check inventory availability
        checkInventoryAvailability(orderLineItems);

        // Calculate total amount
        BigDecimal totalAmount = orderLineItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        
        orderRepository.save(order);
        
        // Update inventory - reduce stock after order is saved
        updateInventoryStock(orderLineItems);
        
        log.info("Order {} is saved and inventory updated", order.getOrderNumber());
        return "Order placed successfully";
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        log.info("Getting orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId);
    }

    public String updateOrder(Long orderId, OrderRequest orderRequest) {
        log.info("Updating order with id: {}", orderId);
        
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        // Update order details
        existingOrder.setCustomerId(orderRequest.getCustomerId());
        
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        
        existingOrder.setOrderLineItemsList(orderLineItems);
        
        // Check inventory availability for updated items
        checkInventoryAvailability(orderLineItems);
        
        // Recalculate total amount
        BigDecimal totalAmount = orderLineItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        existingOrder.setTotalAmount(totalAmount);
        
        orderRepository.save(existingOrder);
        log.info("Order {} is updated", existingOrder.getOrderNumber());
        return "Order updated successfully";
    }

    public Order getOrderById(Long orderId) {
        log.info("Getting order by id: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

    private void checkInventoryAvailability(List<OrderLineItems> orderLineItems) {
        // Extract SKU codes from order items
        List<String> skuCodes = orderLineItems.stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        // Call inventory service to check stock
        log.info("Calling inventory service");

        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())){
            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory/search", uriBuilder ->
                            uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            // Check if all items are in stock
            if (inventoryResponseArray != null) {
                for (OrderLineItems orderItem : orderLineItems) {
                    boolean itemInStock = false;
                    for (InventoryResponse inventoryItem : inventoryResponseArray) {
                        if (inventoryItem.getSkuCode().equals(orderItem.getSkuCode())) {
                            if (inventoryItem.getQuantity() >= orderItem.getQuantity()) {
                                itemInStock = true;
                                break;
                            }
                        }
                    }
                    if (!itemInStock) {
                        throw new RuntimeException("Item with SKU " + orderItem.getSkuCode() + " is not in stock");
                    }
                }
            }
        } finally {
            inventoryServiceLookup.end();
        }


        
        log.info("Inventory check completed for order items");
    }
    
    private void updateInventoryStock(List<OrderLineItems> orderLineItems) {
        log.info("Updating inventory stock for order items");
        
        Span inventoryUpdateSpan = tracer.nextSpan().name("InventoryUpdate");
        
        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryUpdateSpan.start())) {
            for (OrderLineItems orderItem : orderLineItems) {
                // Call inventory service to reduce stock
                webClientBuilder.build()
                    .put()
                    .uri("http://inventory-service/api/inventory/{skuCode}?quantity={quantity}", 
                         orderItem.getSkuCode(), orderItem.getQuantity())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                
                log.info("Reduced stock for SKU: {} by quantity: {}", orderItem.getSkuCode(), orderItem.getQuantity());
            }
        } finally {
            inventoryUpdateSpan.end();
        }
        
        log.info("Inventory stock update completed");
    }
}
