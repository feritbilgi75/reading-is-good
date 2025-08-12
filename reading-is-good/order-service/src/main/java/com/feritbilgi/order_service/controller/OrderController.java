package com.feritbilgi.order_service.controller;

import com.feritbilgi.order_service.dto.OrderRequest;
import com.feritbilgi.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("New order placed: {}", orderRequest);
        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));
    }

    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(() -> "Ooops! Something went wrong, please order after some time!");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<com.feritbilgi.order_service.model.Order> getAllOrders() {
        log.info("Getting all orders");
        return orderService.getAllOrders();
    }

    @GetMapping("/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public List<com.feritbilgi.order_service.model.Order> getOrdersByCustomerId(@PathVariable Long customerId) {
        log.info("Getting orders for customer: {}", customerId);
        return orderService.getOrdersByCustomerId(customerId);
    }

    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public String updateOrder(@PathVariable Long orderId, @RequestBody OrderRequest orderRequest) {
        log.info("Updating order with id: {}", orderId);
        return orderService.updateOrder(orderId, orderRequest);
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public com.feritbilgi.order_service.model.Order getOrderById(@PathVariable Long orderId) {
        log.info("Getting order by id: {}", orderId);
        return orderService.getOrderById(orderId);
    }
}
