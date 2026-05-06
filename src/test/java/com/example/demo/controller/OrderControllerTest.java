package com.example.demo.controller;

import com.example.demo.config.JWTAuthenticationFilter;
import com.example.demo.config.SecurityConfiguration;
import com.example.demo.dto.PlaceOrderRequest;
import com.example.demo.dto.UpdateStatusRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = OrderController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, ServletWebSecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguration.class, JWTAuthenticationFilter.class}
        )
)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean OrderService orderService;
    @MockitoBean UserRepository userRepository;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Order sampleOrder(OrderStatus status) {
        Order order = new Order(null, "Guest", status, new BigDecimal("7.00"), false);
        order.setId(1L);
        return order;
    }

    private User sampleUser() {
        return User.builder()
                .email("alice@test.com")
                .name("Alice")
                .role(Role.CUSTOMER)
                .build();
    }

    // ── POST /api/orders/order ────────────────────────────────────────────────

    @Test
    void placeOrder_guestOrder_returns200() throws Exception {
        PlaceOrderRequest req = new PlaceOrderRequest();
        req.setGuestName("Alice");
        req.setMenuItemIds(List.of(1L));
        req.setSizes(List.of("regular"));
        req.setQuantities(List.of(2));

        when(orderService.placeOrder(eq(null), eq("Alice"), any(), any(), any(), any()))
                .thenReturn(sampleOrder(OrderStatus.NEW));

        mockMvc.perform(post("/api/orders/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("NEW")));
    }

    @Test
    void placeOrder_customerOrder_returns200() throws Exception {
        User user = sampleUser();
        PlaceOrderRequest req = new PlaceOrderRequest();
        req.setCustomerId(1L);
        req.setMenuItemIds(List.of(1L));
        req.setSizes(List.of("regular"));
        req.setQuantities(List.of(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderService.placeOrder(eq(user), any(), any(), any(), any(), any()))
                .thenReturn(sampleOrder(OrderStatus.NEW));

        mockMvc.perform(post("/api/orders/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void placeOrder_customerNotFound_throwsRuntimeException() throws Exception {
        PlaceOrderRequest req = new PlaceOrderRequest();
        req.setCustomerId(99L);
        req.setMenuItemIds(List.of(1L));
        req.setSizes(List.of("regular"));
        req.setQuantities(List.of(1));

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                mockMvc.perform(post("/api/orders/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))))
                .hasMessageContaining("User not found");
    }

    @Test
    void placeOrder_itemNotAvailable_throwsRuntimeException() throws Exception {
        PlaceOrderRequest req = new PlaceOrderRequest();
        req.setMenuItemIds(List.of(1L));
        req.setSizes(List.of("regular"));
        req.setQuantities(List.of(1));

        when(orderService.placeOrder(any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("OldCoffee is not available"));

        assertThatThrownBy(() ->
                mockMvc.perform(post("/api/orders/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))))
                .hasMessageContaining("OldCoffee is not available");
    }

    // ── GET /api/orders/customer/{customerId} ─────────────────────────────────

    @Test
    void getCustomerOrders_returns200WithList() throws Exception {
        User user = sampleUser();
        List<Order> orders = List.of(sampleOrder(OrderStatus.NEW), sampleOrder(OrderStatus.COLLECTED));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderService.getOrderForCustomer(user)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getCustomerOrders_customerNotFound_throwsRuntimeException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                mockMvc.perform(get("/api/orders/customer/99")))
                .hasMessageContaining("User not found");
    }

    // ── GET /api/orders/staff/active ──────────────────────────────────────────

    @Test
    void getActiveOrders_returns200WithList() throws Exception {
        when(orderService.getActiveOrderForStaff())
                .thenReturn(List.of(sampleOrder(OrderStatus.NEW), sampleOrder(OrderStatus.PREPARING)));

        mockMvc.perform(get("/api/orders/staff/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getActiveOrders_emptyList_returns200() throws Exception {
        when(orderService.getActiveOrderForStaff()).thenReturn(List.of());

        mockMvc.perform(get("/api/orders/staff/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ── GET /api/orders/{orderId} ─────────────────────────────────────────────

    @Test
    void getOrderById_found_returns200() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(sampleOrder(OrderStatus.NEW));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("NEW")));
    }

    @Test
    void getOrderById_notFound_throwsRuntimeException() {
        when(orderService.getOrderById(99L)).thenThrow(new RuntimeException("Order not found"));

        assertThatThrownBy(() ->
                mockMvc.perform(get("/api/orders/99")))
                .hasMessageContaining("Order not found");
    }

    // ── PUT /api/orders/{orderId}/status ──────────────────────────────────────

    @Test
    void updateOrderStatus_returns200WithUpdatedOrder() throws Exception {
        Order updated = sampleOrder(OrderStatus.ACCEPTED);
        when(orderService.updateOrderStatus(1L, OrderStatus.ACCEPTED)).thenReturn(updated);

        UpdateStatusRequest req = new UpdateStatusRequest();
        req.setNewStatus(OrderStatus.ACCEPTED);

        mockMvc.perform(put("/api/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACCEPTED")));
    }

    @Test
    void updateOrderStatus_invalidTransition_throwsRuntimeException() throws Exception {
        when(orderService.updateOrderStatus(eq(1L), eq(OrderStatus.COLLECTED)))
                .thenThrow(new RuntimeException("Cannot change status from NEW to COLLECTED"));

        UpdateStatusRequest req = new UpdateStatusRequest();
        req.setNewStatus(OrderStatus.COLLECTED);

        assertThatThrownBy(() ->
                mockMvc.perform(put("/api/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))))
                .hasMessageContaining("Cannot change status");
    }
}
