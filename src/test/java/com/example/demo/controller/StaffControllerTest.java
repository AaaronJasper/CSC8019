package com.example.demo.controller;

import com.example.demo.config.JWTAuthenticationFilter;
import com.example.demo.config.SecurityConfiguration;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.service.StaffService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = StaffController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, ServletWebSecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguration.class, JWTAuthenticationFilter.class}
        )
)
class StaffControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean StaffService staffService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Order orderWithStatus(OrderStatus status) {
        Order order = new Order(null, "Guest", status, BigDecimal.TEN, false);
        order.setId(1L);
        order.setLastStatusChange(LocalDateTime.now());
        return order;
    }

    // ── GET /api/staff/orders ─────────────────────────────────────────────────

    @Test
    void getActiveOrders_returns200WithList() throws Exception {
        when(staffService.getActiveOrders()).thenReturn(
                List.of(orderWithStatus(OrderStatus.NEW), orderWithStatus(OrderStatus.PREPARING)));

        mockMvc.perform(get("/api/staff/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("NEW")))
                .andExpect(jsonPath("$[1].status", is("PREPARING")));
    }

    @Test
    void getActiveOrders_emptyList_returns200() throws Exception {
        when(staffService.getActiveOrders()).thenReturn(List.of());

        mockMvc.perform(get("/api/staff/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ── GET /api/staff/orders/archive ─────────────────────────────────────────

    @Test
    void getArchivedOrders_returns200WithList() throws Exception {
        Order archived = orderWithStatus(OrderStatus.COLLECTED);
        archived.setArchived(true);
        when(staffService.getArchivedOrders()).thenReturn(List.of(archived));

        mockMvc.perform(get("/api/staff/orders/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].archived", is(true)));
    }

    @Test
    void getArchivedOrders_emptyList_returns200() throws Exception {
        when(staffService.getArchivedOrders()).thenReturn(List.of());

        mockMvc.perform(get("/api/staff/orders/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ── PATCH /api/staff/orders/{id}/status ───────────────────────────────────

    @Test
    void updateOrderStatus_newToAccepted_returns200() throws Exception {
        when(staffService.updateOrderStatus(1L, OrderStatus.ACCEPTED))
                .thenReturn(orderWithStatus(OrderStatus.ACCEPTED));

        mockMvc.perform(patch("/api/staff/orders/1/status")
                        .param("status", "ACCEPTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACCEPTED")));
    }

    @Test
    void updateOrderStatus_toCancelled_returns200() throws Exception {
        Order cancelled = orderWithStatus(OrderStatus.CANCELLED);
        cancelled.setArchived(true);
        when(staffService.updateOrderStatus(1L, OrderStatus.CANCELLED)).thenReturn(cancelled);

        mockMvc.perform(patch("/api/staff/orders/1/status")
                        .param("status", "CANCELLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")))
                .andExpect(jsonPath("$.archived", is(true)));
    }

    @Test
    void updateOrderStatus_invalidTransition_throwsRuntimeException() {
        when(staffService.updateOrderStatus(1L, OrderStatus.COLLECTED))
                .thenThrow(new RuntimeException("Invalid status transition: NEW -> COLLECTED"));

        assertThatThrownBy(() ->
                mockMvc.perform(patch("/api/staff/orders/1/status")
                        .param("status", "COLLECTED")))
                .hasMessageContaining("Invalid status transition");
    }

    @Test
    void updateOrderStatus_orderNotFound_throwsRuntimeException() {
        when(staffService.updateOrderStatus(99L, OrderStatus.ACCEPTED))
                .thenThrow(new RuntimeException("Order not found with id: 99"));

        assertThatThrownBy(() ->
                mockMvc.perform(patch("/api/staff/orders/99/status")
                        .param("status", "ACCEPTED")))
                .hasMessageContaining("Order not found");
    }

    @Test
    void updateOrderStatus_invalidStatusValue_returns400() throws Exception {
        mockMvc.perform(patch("/api/staff/orders/1/status")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }
}
