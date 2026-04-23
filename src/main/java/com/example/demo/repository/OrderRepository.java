package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders that are not archived (active dashboard)
    List<Order> findByArchivedFalseOrderByCreatedAtDesc();

    // Find all archived orders (archive dashboard)
    List<Order> findByArchivedTrueOrderByCreatedAtDesc();

    // Find all orders by a specific status
    List<Order> findByStatus(OrderStatus status);

    // Find orders that are overdue for auto-cancellation
    // (status is NEW or ACCEPTED, and pickup time has passed by more than 15 minutes)
    List<Order> findByStatusInAndPickupTimeBefore(List<OrderStatus> statuses, LocalDateTime cutoffTime);
    List<Order> findByArchivedFalseAndStatusOrderByCreatedAtDesc(OrderStatus status);
}
