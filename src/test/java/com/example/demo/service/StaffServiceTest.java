package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.User;
import com.example.demo.entity.Role;
import com.example.demo.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock OrderRepository orderRepository;

    @InjectMocks StaffService staffService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Order orderWithStatus(OrderStatus status) {
        Order order = new Order(null, "Guest", status, BigDecimal.TEN, false);
        order.setLastStatusChange(LocalDateTime.now());
        order.setArchived(false);
        return order;
    }

    // ── getActiveOrders ───────────────────────────────────────────────────────

    @Test
    void getActiveOrders_returnsResultFromRepository() {
        List<Order> expected = List.of(orderWithStatus(OrderStatus.NEW));
        when(orderRepository.findByStatusInOrderByOrderTimeDesc(any())).thenReturn(expected);

        assertThat(staffService.getActiveOrders()).isEqualTo(expected);
    }

    @Test
    void getActiveOrders_queriesCorrectStatuses() {
        when(orderRepository.findByStatusInOrderByOrderTimeDesc(any())).thenReturn(List.of());

        staffService.getActiveOrders();

        verify(orderRepository).findByStatusInOrderByOrderTimeDesc(
                argThat(list -> list.containsAll(
                        List.of(OrderStatus.NEW, OrderStatus.ACCEPTED, OrderStatus.PREPARING, OrderStatus.READY)
                ))
        );
    }

    // ── getArchivedOrders ─────────────────────────────────────────────────────

    @Test
    void getArchivedOrders_returnsResultFromRepository() {
        List<Order> expected = List.of(orderWithStatus(OrderStatus.COLLECTED));
        when(orderRepository.findByArchivedTrueOrderByOrderTimeDesc()).thenReturn(expected);

        assertThat(staffService.getArchivedOrders()).isEqualTo(expected);
    }

    // ── updateOrderStatus ─────────────────────────────────────────────────────

    @Test
    void updateOrderStatus_newToAccepted_succeeds() {
        Order order = orderWithStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = staffService.updateOrderStatus(1L, OrderStatus.ACCEPTED);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void updateOrderStatus_newToCancelled_succeeds() {
        Order order = orderWithStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThat(staffService.updateOrderStatus(1L, OrderStatus.CANCELLED).getStatus())
                .isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void updateOrderStatus_acceptedToPreparing_succeeds() {
        Order order = orderWithStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThat(staffService.updateOrderStatus(1L, OrderStatus.PREPARING).getStatus())
                .isEqualTo(OrderStatus.PREPARING);
    }

    @Test
    void updateOrderStatus_preparingToReady_succeeds() {
        Order order = orderWithStatus(OrderStatus.PREPARING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThat(staffService.updateOrderStatus(1L, OrderStatus.READY).getStatus())
                .isEqualTo(OrderStatus.READY);
    }

    @Test
    void updateOrderStatus_readyToCollected_setsArchived() {
        Order order = orderWithStatus(OrderStatus.READY);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = staffService.updateOrderStatus(1L, OrderStatus.COLLECTED);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.COLLECTED);
        assertThat(result.isArchived()).isTrue();
    }

    @Test
    void updateOrderStatus_toCancelled_setsArchived() {
        Order order = orderWithStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = staffService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        assertThat(result.isArchived()).isTrue();
    }

    @Test
    void updateOrderStatus_invalidTransition_throws() {
        Order order = orderWithStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> staffService.updateOrderStatus(1L, OrderStatus.COLLECTED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid status transition");
    }

    @Test
    void updateOrderStatus_collectedToAny_throws() {
        Order order = orderWithStatus(OrderStatus.COLLECTED);
        order.setArchived(true);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> staffService.updateOrderStatus(1L, OrderStatus.CANCELLED))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void updateOrderStatus_archivedOrder_throws() {
        Order order = orderWithStatus(OrderStatus.COLLECTED);
        order.setArchived(true);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> staffService.updateOrderStatus(1L, OrderStatus.CANCELLED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Archived");
    }

    @Test
    void updateOrderStatus_sameStatus_returnsOrderUnchanged() {
        Order order = orderWithStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = staffService.updateOrderStatus(1L, OrderStatus.NEW);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.NEW);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_orderNotFound_throws() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.updateOrderStatus(99L, OrderStatus.ACCEPTED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void updateOrderStatus_updatesLastStatusChange() {
        Order order = orderWithStatus(OrderStatus.NEW);
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = staffService.updateOrderStatus(1L, OrderStatus.ACCEPTED);

        assertThat(result.getLastStatusChange()).isAfter(before);
    }

    // ── autoCancel ────────────────────────────────────────────────────────────

    @Test
    void autoCancel_cancelsOverdueNewOrders() {
        Order overdue = orderWithStatus(OrderStatus.NEW);
        when(orderRepository.findByStatusInAndLastStatusChangeBefore(any(), any()))
                .thenReturn(List.of(overdue));

        staffService.autoCancel();

        assertThat(overdue.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(overdue.isArchived()).isTrue();
        verify(orderRepository).saveAll(List.of(overdue));
    }

    @Test
    void autoCancel_cancelsMultipleOverdueOrders() {
        Order order1 = orderWithStatus(OrderStatus.NEW);
        Order order2 = orderWithStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findByStatusInAndLastStatusChangeBefore(any(), any()))
                .thenReturn(List.of(order1, order2));

        staffService.autoCancel();

        assertThat(order1.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order2.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).saveAll(List.of(order1, order2));
    }

    @Test
    void autoCancel_noOverdueOrders_savesNothing() {
        when(orderRepository.findByStatusInAndLastStatusChangeBefore(any(), any()))
                .thenReturn(List.of());

        staffService.autoCancel();

        verify(orderRepository).saveAll(List.of());
    }
}
