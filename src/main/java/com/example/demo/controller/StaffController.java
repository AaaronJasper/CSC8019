package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    // GET all active orders for the main dashboard
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getActiveOrders() {
        return ResponseEntity.ok(staffService.getActiveOrders());
    }

    // GET all archived orders for the archive dashboard
    @GetMapping("/orders/archive")
    public ResponseEntity<List<Order>> getArchivedOrders() {
        return ResponseEntity.ok(staffService.getArchivedOrders());
    }

    // PATCH update the status of a specific order
    // Valid statuses: NEW, ACCEPTED, PREPARING, READY, COLLECTED, CANCELLED
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        Order updated = staffService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}