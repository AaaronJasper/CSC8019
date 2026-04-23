package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.User;
//import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByStatusIn(List<OrderStatus> statuses);
    List<Order> findByStatusAndLastStatusChangeBefore(OrderStatus status, LocalDateTime cutoffTime);
    List<Order> findByStatusInAndLastStatusChangeBefore(List<OrderStatus> statuses, LocalDateTime cutoffTime);
    List<Order> findByStatusInOrderByOrderTimeDesc(List<OrderStatus> statuses);
    List<Order> findByArchivedFalseOrderByOrderTimeDesc();
    List<Order> findByArchivedTrueOrderByOrderTimeDesc();
}
