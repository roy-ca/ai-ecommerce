package com.roy.ecommerce.controller;

import com.roy.ecommerce.dto.OrderResponse;
import com.roy.ecommerce.dto.OrderStatusUpdateRequest;
import com.roy.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse placeOrder(Authentication authentication) {
        String email = authentication.getName();
        return orderService.placeOrder(email);
    }

    @GetMapping
    public List<OrderResponse> getOrders(Authentication authentication) {
        String email = authentication.getName();
        return orderService.getOrders(email);
    }

    @PutMapping("/{orderId}/status")
    public OrderResponse updateOrderStatus(@PathVariable Long orderId, @Valid @RequestBody OrderStatusUpdateRequest request) {
        return orderService.updateOrderStatus(orderId, request);
    }
}
