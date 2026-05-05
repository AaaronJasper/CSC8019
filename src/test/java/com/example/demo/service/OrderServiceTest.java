package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderItemRepo orderItemRepo;
    @Mock UserRepository userRepository;
    @Mock MenuItemRepository menuItemRepository;
    @Mock CustomerRepository customerRepository;
    @Mock StaffRepository staffRepository;

    @InjectMocks OrderService orderService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "loyaltyBuyCount", 9);
        ReflectionTestUtils.setField(orderService, "openingHour", 6);
        ReflectionTestUtils.setField(orderService, "closingHour", 22);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private MenuItem menuItemWithRegularPrice(String name, BigDecimal price) {
        MenuItem item = new MenuItem(name, "desc", "url", BigDecimal.ONE, true, "drinks", 10);
        MenuItemSizePrice sp = new MenuItemSizePrice(item, "regular", price);
        item.getSizePrices().add(sp);
        return item;
    }

    private MenuItem menuItemWithLargePrice(String name, BigDecimal price) {
        MenuItem item = new MenuItem(name, "desc", "url", BigDecimal.ONE, true, "drinks", 10);
        MenuItemSizePrice sp = new MenuItemSizePrice(item, "large", price);
        item.getSizePrices().add(sp);
        return item;
    }

    private Order orderWithStatus(OrderStatus status) {
        Order order = new Order(null, "Guest", status, BigDecimal.TEN, false);
        order.setLastStatusChange(LocalDateTime.now());
        return order;
    }

    private Order orderWithCustomerAndStatus(User customer, OrderStatus status) {
        Order order = new Order(customer, null, status, BigDecimal.TEN, false);
        order.setLastStatusChange(LocalDateTime.now());
        return order;
    }

    // ── placeOrder ───────────────────────────────────────────────────────────

    @Test
    void placeOrder_guestOrder_calculatesTotal() {
        MenuItem item = menuItemWithRegularPrice("Latte", new BigDecimal("3.50"));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.placeOrder(null, "Alice", List.of(1L), List.of("regular"), List.of(2));

        assertThat(result.getTotalPrice()).isEqualByComparingTo("7.00");
        assertThat(result.getCustomer()).isNull();
        assertThat(result.getGuestName()).isEqualTo("Alice");
    }

    @Test
    void placeOrder_withCustomer_assignsCustomer() {
        User user = User.builder().email("alice@test.com").name("Alice").role(Role.CUSTOMER).build();
        MenuItem item = menuItemWithRegularPrice("Espresso", new BigDecimal("2.00"));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.placeOrder(user, null, List.of(1L), List.of("regular"), List.of(1));

        assertThat(result.getCustomer()).isEqualTo(user);
    }

    @Test
    void placeOrder_largeSize_usesLargePrice() {
        MenuItem item = menuItemWithLargePrice("Latte", new BigDecimal("4.50"));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.placeOrder(null, "Bob", List.of(1L), List.of("LARGE"), List.of(1));

        assertThat(result.getTotalPrice()).isEqualByComparingTo("4.50");
    }

    @Test
    void placeOrder_multipleItems_sumsTotal() {
        MenuItem item1 = menuItemWithRegularPrice("Latte", new BigDecimal("3.00"));
        MenuItem item2 = menuItemWithRegularPrice("Muffin", new BigDecimal("2.00"));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(menuItemRepository.findById(2L)).thenReturn(Optional.of(item2));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.placeOrder(null, "Bob",
                List.of(1L, 2L), List.of("regular", "regular"), List.of(2, 3));

        // 3.00*2 + 2.00*3 = 12.00
        assertThat(result.getTotalPrice()).isEqualByComparingTo("12.00");
    }

    @Test
    void placeOrder_itemNotFound_throws() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.placeOrder(null, "Guest", List.of(99L), List.of("regular"), List.of(1)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Item not found");
    }

    @Test
    void placeOrder_itemNotAvailable_throws() {
        MenuItem item = new MenuItem("OldCoffee", "desc", "url", BigDecimal.ONE, false, "drinks", 0);
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                orderService.placeOrder(null, "Guest", List.of(1L), List.of("regular"), List.of(1)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void placeOrder_requestedSizeNotOnItem_throws() {
        // item only has large price, request regular
        MenuItem item = menuItemWithLargePrice("Latte", new BigDecimal("4.50"));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                orderService.placeOrder(null, "Guest", List.of(1L), List.of("regular"), List.of(1)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("does not have size");
    }

    // ── updateOrderStatus / validateStatusTransition ──────────────────────────

    @Test
    void updateOrderStatus_newToAccepted_succeeds() {
        Order order = orderWithStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.updateOrderStatus(1L, OrderStatus.ACCEPTED);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    void updateOrderStatus_newToCancelled_succeeds() {
        Order order = orderWithStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void updateOrderStatus_acceptedToPreparing_succeeds() {
        Order order = orderWithStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThat(orderService.updateOrderStatus(1L, OrderStatus.PREPARING).getStatus())
                .isEqualTo(OrderStatus.PREPARING);
    }

    @Test
    void updateOrderStatus_preparingToReady_succeeds() {
        Order order = orderWithStatus(OrderStatus.PREPARING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThat(orderService.updateOrderStatus(1L, OrderStatus.READY).getStatus())
                .isEqualTo(OrderStatus.READY);
    }

    @Test
    void updateOrderStatus_readyToCollected_succeeds() {
        Order order = orderWithStatus(OrderStatus.READY);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThat(orderService.updateOrderStatus(1L, OrderStatus.COLLECTED).getStatus())
                .isEqualTo(OrderStatus.COLLECTED);
    }

    @Test
    void updateOrderStatus_newToPreparing_throws() {
        Order order = orderWithStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.PREPARING))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot change status");
    }

    @Test
    void updateOrderStatus_collectedToAny_throws() {
        Order order = orderWithStatus(OrderStatus.COLLECTED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.CANCELLED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot change status");
    }

    @Test
    void updateOrderStatus_cancelledToAny_throws() {
        Order order = orderWithStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.ACCEPTED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot change status");
    }

    @Test
    void updateOrderStatus_orderNotFound_throws() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderStatus(99L, OrderStatus.ACCEPTED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void updateOrderStatus_setsLastStatusChange() {
        Order order = orderWithStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.updateOrderStatus(1L, OrderStatus.ACCEPTED);

        assertThat(result.getLastStatusChange()).isNotNull();
    }

    // ── handleLoyalty (tested via updateOrderStatus with COLLECTED) ───────────

    @Test
    void handleLoyalty_incrementsCompletedOrdersAndLoyaltyCount() {
        User customer = User.builder().email("c@test.com").name("C").role(Role.CUSTOMER).build();
        customer.setLoyaltyCount(3);
        customer.setTotalCompletedOrders(5);
        Order order = orderWithCustomerAndStatus(customer, OrderStatus.READY);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        orderService.updateOrderStatus(1L, OrderStatus.COLLECTED);

        assertThat(customer.getLoyaltyCount()).isEqualTo(4);
        assertThat(customer.getTotalCompletedOrders()).isEqualTo(6);
        verify(userRepository).save(customer);
    }

    @Test
    void handleLoyalty_resetsCountAtThreshold() {
        User customer = User.builder().email("c@test.com").name("C").role(Role.CUSTOMER).build();
        customer.setLoyaltyCount(8); // one away from threshold of 9
        Order order = orderWithCustomerAndStatus(customer, OrderStatus.READY);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        orderService.updateOrderStatus(1L, OrderStatus.COLLECTED);

        assertThat(customer.getLoyaltyCount()).isEqualTo(0);
    }

    @Test
    void handleLoyalty_guestOrder_skipsLoyalty() {
        Order order = orderWithStatus(OrderStatus.READY); // no customer
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        orderService.updateOrderStatus(1L, OrderStatus.COLLECTED);

        verify(userRepository, never()).save(any());
    }

    // ── query methods ─────────────────────────────────────────────────────────

    @Test
    void getOrderForCustomer_returnsCustomerOrders() {
        User customer = User.builder().email("c@test.com").name("C").role(Role.CUSTOMER).build();
        List<Order> expected = List.of(orderWithStatus(OrderStatus.NEW));
        when(orderRepository.findByCustomer(customer)).thenReturn(expected);

        assertThat(orderService.getOrderForCustomer(customer)).isEqualTo(expected);
    }

    @Test
    void getActiveOrderForStaff_returnsActiveOrders() {
        List<Order> expected = List.of(orderWithStatus(OrderStatus.NEW));
        when(orderRepository.findByStatusIn(any())).thenReturn(expected);

        assertThat(orderService.getActiveOrderForStaff()).isEqualTo(expected);
    }

    @Test
    void getOrderById_found_returnsOrder() {
        Order order = orderWithStatus(OrderStatus.NEW);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThat(orderService.getOrderById(1L)).isEqualTo(order);
    }

    @Test
    void getOrderById_notFound_throws() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Order not found");
    }
}
