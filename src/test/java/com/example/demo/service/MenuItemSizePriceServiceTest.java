package com.example.demo.service;

import com.example.demo.dto.MenuItemSizePriceRequest;
import com.example.demo.dto.MenuItemSizePriceUpdateRequest;
import com.example.demo.entity.MenuItem;
import com.example.demo.entity.MenuItemSizePrice;
import com.example.demo.exception.MenuItemNotFoundException;
import com.example.demo.exception.MenuItemSizePriceNotFoundException;
import com.example.demo.exception.NoFieldsProvidedException;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.repository.MenuItemSizePriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemSizePriceServiceTest {

    @Mock MenuItemRepository menuItemRepository;
    @Mock MenuItemSizePriceRepository sizePriceRepository;

    @InjectMocks MenuItemSizePriceService service;

    // ── helpers ───────────────────────────────────────────────────────────────

    private MenuItem sampleMenuItem() {
        return new MenuItem("Latte", "desc", "url", new BigDecimal("4.5"), true, "drinks", 10);
    }

    private MenuItemSizePrice sampleSizePrice(MenuItem item) {
        return new MenuItemSizePrice(item, "regular", new BigDecimal("3.50"));
    }

    // ── createSizePrice ───────────────────────────────────────────────────────

    @Test
    void createSizePrice_success_returnsSavedEntry() {
        MenuItem item = sampleMenuItem();
        MenuItemSizePriceRequest req = new MenuItemSizePriceRequest();
        req.setMenuItemId(1L);
        req.setSize("regular");
        req.setPrice(new BigDecimal("3.50"));

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(sizePriceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItemSizePrice result = service.createSizePrice(req);

        assertThat(result.getSize()).isEqualTo("regular");
        assertThat(result.getPrice()).isEqualByComparingTo("3.50");
        assertThat(result.getMenuItem()).isEqualTo(item);
    }

    @Test
    void createSizePrice_addsEntryToMenuItemSizePrices() {
        MenuItem item = sampleMenuItem();
        MenuItemSizePriceRequest req = new MenuItemSizePriceRequest();
        req.setMenuItemId(1L);
        req.setSize("large");
        req.setPrice(new BigDecimal("4.50"));

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(sizePriceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.createSizePrice(req);

        assertThat(item.getSizePrices()).hasSize(1);
        assertThat(item.getSizePrices().get(0).getSize()).isEqualTo("large");
    }

    @Test
    void createSizePrice_menuItemNotFound_throwsMenuItemNotFoundException() {
        MenuItemSizePriceRequest req = new MenuItemSizePriceRequest();
        req.setMenuItemId(99L);
        req.setSize("regular");
        req.setPrice(new BigDecimal("3.00"));

        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createSizePrice(req))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── updateSizePrice ───────────────────────────────────────────────────────

    @Test
    void updateSizePrice_updatesSize() {
        MenuItem item = sampleMenuItem();
        MenuItemSizePrice existing = sampleSizePrice(item);
        when(sizePriceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(sizePriceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItemSizePriceUpdateRequest req = new MenuItemSizePriceUpdateRequest();
        req.setSize("large");

        MenuItemSizePrice result = service.updateSizePrice(1L, req);

        assertThat(result.getSize()).isEqualTo("large");
    }

    @Test
    void updateSizePrice_updatesPrice() {
        MenuItem item = sampleMenuItem();
        MenuItemSizePrice existing = sampleSizePrice(item);
        when(sizePriceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(sizePriceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItemSizePriceUpdateRequest req = new MenuItemSizePriceUpdateRequest();
        req.setPrice(new BigDecimal("5.00"));

        MenuItemSizePrice result = service.updateSizePrice(1L, req);

        assertThat(result.getPrice()).isEqualByComparingTo("5.00");
    }

    @Test
    void updateSizePrice_updatesBothFields() {
        MenuItem item = sampleMenuItem();
        MenuItemSizePrice existing = sampleSizePrice(item);
        when(sizePriceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(sizePriceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MenuItemSizePriceUpdateRequest req = new MenuItemSizePriceUpdateRequest();
        req.setSize("large");
        req.setPrice(new BigDecimal("5.50"));

        MenuItemSizePrice result = service.updateSizePrice(1L, req);

        assertThat(result.getSize()).isEqualTo("large");
        assertThat(result.getPrice()).isEqualByComparingTo("5.50");
    }

    @Test
    void updateSizePrice_noFieldsProvided_throwsNoFieldsProvidedException() {
        MenuItem item = sampleMenuItem();
        MenuItemSizePrice existing = sampleSizePrice(item);
        when(sizePriceRepository.findById(1L)).thenReturn(Optional.of(existing));

        MenuItemSizePriceUpdateRequest req = new MenuItemSizePriceUpdateRequest(); // all null

        assertThatThrownBy(() -> service.updateSizePrice(1L, req))
                .isInstanceOf(NoFieldsProvidedException.class)
                .hasMessageContaining("At least one field");
    }

    @Test
    void updateSizePrice_notFound_throwsMenuItemSizePriceNotFoundException() {
        when(sizePriceRepository.findById(99L)).thenReturn(Optional.empty());

        MenuItemSizePriceUpdateRequest req = new MenuItemSizePriceUpdateRequest();
        req.setSize("regular");

        assertThatThrownBy(() -> service.updateSizePrice(99L, req))
                .isInstanceOf(MenuItemSizePriceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
