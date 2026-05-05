package com.example.demo.service;

import com.example.demo.dto.MenuItemRequest;
import com.example.demo.dto.MenuItemUpdateRequest;
import com.example.demo.entity.MenuItem;
import com.example.demo.exception.MenuItemNotFoundException;
import com.example.demo.exception.MenuItemsNotFoundException;
import com.example.demo.exception.NoFieldsProvidedException;
import com.example.demo.repository.MenuItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock MenuItemRepository menuItemRepository;

    @InjectMocks MenuService menuService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private MenuItem sampleItem() {
        return new MenuItem("Latte", "A coffee", "http://img.url",
                new BigDecimal("4.5"), true, "drinks", 20);
    }

    private MenuItemRequest sampleRequest() {
        MenuItemRequest req = new MenuItemRequest();
        req.setName("Espresso");
        req.setDescription("Strong coffee");
        req.setImgUrl("http://img.url");
        req.setRating(new BigDecimal("4.5"));
        req.setCategory("drinks");
        req.setItemCount(15);
        return req;
    }

    // ── getAllMenuItems ────────────────────────────────────────────────────────

    @Test
    void getAllMenuItems_returnsList() {
        List<MenuItem> items = List.of(sampleItem(), sampleItem());
        when(menuItemRepository.findAll()).thenReturn(items);

        assertThat(menuService.getAllMenuItems()).hasSize(2);
    }

    @Test
    void getAllMenuItems_empty_throwsMenuItemsNotFoundException() {
        when(menuItemRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> menuService.getAllMenuItems())
                .isInstanceOf(MenuItemsNotFoundException.class)
                .hasMessageContaining("No menu items found");
    }

    // ── getMenuItemById ───────────────────────────────────────────────────────

    @Test
    void getMenuItemById_found_returnsItem() {
        MenuItem item = sampleItem();
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThat(menuService.getMenuItemById(1L)).isEqualTo(item);
    }

    @Test
    void getMenuItemById_notFound_throwsMenuItemNotFoundException() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getMenuItemById(99L))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── addMenuItem ───────────────────────────────────────────────────────────

    @Test
    void addMenuItem_savesWithAvailableTrue() {
        MenuItemRequest req = sampleRequest();
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItem result = menuService.addMenuItem(req);

        assertThat(result.getName()).isEqualTo("Espresso");
        assertThat(result.getIsAvailable()).isTrue();
        assertThat(result.getCategory()).isEqualTo("drinks");
        assertThat(result.getItemCount()).isEqualTo(15);
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    void addMenuItem_mapsAllFields() {
        MenuItemRequest req = sampleRequest();
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItem result = menuService.addMenuItem(req);

        assertThat(result.getDescription()).isEqualTo("Strong coffee");
        assertThat(result.getImgUrl()).isEqualTo("http://img.url");
        assertThat(result.getRating()).isEqualByComparingTo("4.5");
    }

    // ── updateMenuItem ────────────────────────────────────────────────────────

    @Test
    void updateMenuItem_updatesName() {
        MenuItem item = sampleItem();
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItemUpdateRequest req = new MenuItemUpdateRequest();
        req.setName("Flat White");

        MenuItem result = menuService.updateMenuItem(1L, req);

        assertThat(result.getName()).isEqualTo("Flat White");
    }

    @Test
    void updateMenuItem_updatesDescription() {
        MenuItem item = sampleItem();
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItemUpdateRequest req = new MenuItemUpdateRequest();
        req.setDescription("New description");

        MenuItem result = menuService.updateMenuItem(1L, req);

        assertThat(result.getDescription()).isEqualTo("New description");
    }

    @Test
    void updateMenuItem_updatesRating() {
        MenuItem item = sampleItem();
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItemUpdateRequest req = new MenuItemUpdateRequest();
        req.setRating(new BigDecimal("3.5"));

        MenuItem result = menuService.updateMenuItem(1L, req);

        assertThat(result.getRating()).isEqualByComparingTo("3.5");
    }

    @Test
    void updateMenuItem_updatesCategory() {
        MenuItem item = sampleItem();
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItemUpdateRequest req = new MenuItemUpdateRequest();
        req.setCategory("pastries");

        MenuItem result = menuService.updateMenuItem(1L, req);

        assertThat(result.getCategory()).isEqualTo("pastries");
    }

    @Test
    void updateMenuItem_updatesItemCount() {
        MenuItem item = sampleItem();
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItemUpdateRequest req = new MenuItemUpdateRequest();
        req.setItemCount(50);

        MenuItem result = menuService.updateMenuItem(1L, req);

        assertThat(result.getItemCount()).isEqualTo(50);
    }

    @Test
    void updateMenuItem_updatesMultipleFields() {
        MenuItem item = sampleItem();
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(menuItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItemUpdateRequest req = new MenuItemUpdateRequest();
        req.setName("Cappuccino");
        req.setCategory("hot drinks");
        req.setItemCount(30);

        MenuItem result = menuService.updateMenuItem(1L, req);

        assertThat(result.getName()).isEqualTo("Cappuccino");
        assertThat(result.getCategory()).isEqualTo("hot drinks");
        assertThat(result.getItemCount()).isEqualTo(30);
    }

    @Test
    void updateMenuItem_noFieldsProvided_throwsNoFieldsProvidedException() {
        MenuItem item = sampleItem();
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));

        MenuItemUpdateRequest req = new MenuItemUpdateRequest(); // all null

        assertThatThrownBy(() -> menuService.updateMenuItem(1L, req))
                .isInstanceOf(NoFieldsProvidedException.class)
                .hasMessageContaining("At least one field");
    }

    @Test
    void updateMenuItem_notFound_throwsMenuItemNotFoundException() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        MenuItemUpdateRequest req = new MenuItemUpdateRequest();
        req.setName("Something");

        assertThatThrownBy(() -> menuService.updateMenuItem(99L, req))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── softDeleteMenuItem ────────────────────────────────────────────────────

    @Test
    void softDeleteMenuItem_setsIsAvailableFalse() {
        MenuItem item = sampleItem(); // isAvailable = true
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));

        menuService.softDeleteMenuItem(1L);

        assertThat(item.getIsAvailable()).isFalse();
        verify(menuItemRepository).save(item);
    }

    @Test
    void softDeleteMenuItem_returnsConfirmationMessage() {
        MenuItem item = sampleItem();
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));

        String message = menuService.softDeleteMenuItem(1L);

        assertThat(message).contains("1").contains("soft-deleted");
    }

    @Test
    void softDeleteMenuItem_notFound_throwsMenuItemNotFoundException() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.softDeleteMenuItem(99L))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessageContaining("99");
    }
}
