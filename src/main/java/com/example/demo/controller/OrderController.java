package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")

public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping("/order")
    public ResponseEntity<Order> placeOrder(@RequestBody PlaceOrderRequest request){
        User customer = null;
        if(request.getCustomerId() != null){
            customer = userRepository.findById(request.getCustomerId()).orElseThrow(() -> new RuntimeException("User not found"));
        }

        Order order = orderService.placeOrder(customer, request.getGuestName(), request.getMenuItemIds(), request.getSizes(), request.getQuantities());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getCustomerOrder(@PathVariable Long customerId) {
        User customer = userRepository.findById(customerId).orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderService.getOrderForCustomer(customer);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/staff/active")
    public ResponseEntity<List<Order>> getActiveOrders(){
        List<Order>orders = orderService.getActiveOrderForStaff();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order>getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateStatusRequest request
    ) {
        Order order = orderService.updateOrderStatus(orderId, request.getNewStatus());
        return ResponseEntity.ok(order);
    }
    }

