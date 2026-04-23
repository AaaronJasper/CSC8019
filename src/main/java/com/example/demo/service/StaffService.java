package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class StaffService {

    @Autowired
    private OrderRepository orderRepository;

    private static final Map<OrderStatus, List<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.NEW, List.of(OrderStatus.ACCEPTED, OrderStatus.CANCELLED),
            OrderStatus.ACCEPTED, List.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
            OrderStatus.PREPARING, List.of(OrderStatus.READY, OrderStatus.CANCELLED),
            OrderStatus.READY, List.of(OrderStatus.COLLECTED, OrderStatus.CANCELLED),
            OrderStatus.COLLECTED, List.of(),
            OrderStatus.CANCELLED, List.of()
    );

    public List<Order> getActiveOrders() {
        return orderRepository.findByArchivedFalseOrderByCreatedAtDesc();
    }

    public List<Order> getArchivedOrders() {
        return orderRepository.findByArchivedTrueOrderByCreatedAtDesc();
    }

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        OrderStatus currentStatus = order.getStatus();

        if (order.isArchived()) {
            throw new RuntimeException("Archived orders cannot be updated");
        }

        if (currentStatus == newStatus) {
            return order;
        }

        List<OrderStatus> allowedNextStatuses = ALLOWED_TRANSITIONS.get(currentStatus);
        if (allowedNextStatuses == null || !allowedNextStatuses.contains(newStatus)) {
            throw new RuntimeException(
                    "Invalid status transition: " + currentStatus + " -> " + newStatus
            );
        }

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.COLLECTED || newStatus == OrderStatus.CANCELLED) {
            order.setArchived(true);
        }

        return orderRepository.save(order);
    }

    @Scheduled(fixedRate = 60000)
    public void autoCancel() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);

        List<Order> overdueOrders = orderRepository.findByStatusInAndPickupTimeBefore(
                List.of(OrderStatus.NEW, OrderStatus.ACCEPTED),
                cutoff
        );

        for (Order order : overdueOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setArchived(true);
        }

        orderRepository.saveAll(overdueOrders);
    }
}