package com.example.demo.controller;

import com.example.demo.config.JWTAuthenticationFilter;
import com.example.demo.config.SecurityConfiguration;
import com.example.demo.entity.MenuItem;
import com.example.demo.entity.MenuItemSizePrice;
import com.example.demo.exception.MenuItemNotFoundException;
import com.example.demo.exception.MenuItemSizePriceNotFoundException;
import com.example.demo.exception.NoFieldsProvidedException;
import com.example.demo.service.MenuItemSizePriceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MenuItemSizePriceController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, ServletWebSecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguration.class, JWTAuthenticationFilter.class}
        )
)
class MenuItemSizePriceControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean MenuItemSizePriceService sizePriceService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private MenuItemSizePrice sampleSizePrice() {
        MenuItem item = new MenuItem("Latte", "desc", "url", new BigDecimal("4.5"), true, "drinks", 10);
        return new MenuItemSizePrice(item, "regular", new BigDecimal("3.50"));
    }

    // ── POST /api/menu/size-prices ────────────────────────────────────────────

    @Test
    void createSizePrice_validRequest_returns201() throws Exception {
        when(sizePriceService.createSizePrice(any())).thenReturn(sampleSizePrice());

        mockMvc.perform(multipart("/api/menu/size-prices")
                        .param("menuItemId", "1")
                        .param("size", "regular")
                        .param("price", "3.50"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.size", is("regular")))
                .andExpect(jsonPath("$.price", is(3.50)));
    }

    @Test
    void createSizePrice_menuItemNotFound_returns404() throws Exception {
        when(sizePriceService.createSizePrice(any())).thenThrow(new MenuItemNotFoundException(99L));

        mockMvc.perform(multipart("/api/menu/size-prices")
                        .param("menuItemId", "99")
                        .param("size", "regular")
                        .param("price", "3.50"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void createSizePrice_missingMenuItemId_returns400() throws Exception {
        mockMvc.perform(multipart("/api/menu/size-prices")
                        .param("size", "regular")
                        .param("price", "3.50"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSizePrice_missingSize_returns400() throws Exception {
        mockMvc.perform(multipart("/api/menu/size-prices")
                        .param("menuItemId", "1")
                        .param("price", "3.50"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSizePrice_priceExceedsMax_returns400() throws Exception {
        mockMvc.perform(multipart("/api/menu/size-prices")
                        .param("menuItemId", "1")
                        .param("size", "regular")
                        .param("price", "9999.00"))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/menu/size-prices/{id} ────────────────────────────────────────

    @Test
    void updateSizePrice_validRequest_returns200() throws Exception {
        MenuItemSizePrice updated = sampleSizePrice();
        updated.setSize("large");
        updated.setPrice(new BigDecimal("4.50"));
        when(sizePriceService.updateSizePrice(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/menu/size-prices/1")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("size", "large")
                        .param("price", "4.50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", is("large")))
                .andExpect(jsonPath("$.price", is(4.50)));
    }

    @Test
    void updateSizePrice_notFound_returns404() throws Exception {
        when(sizePriceService.updateSizePrice(eq(99L), any()))
                .thenThrow(new MenuItemSizePriceNotFoundException(99L));

        mockMvc.perform(put("/api/menu/size-prices/99")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("size", "large"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSizePrice_noFieldsProvided_returns400() throws Exception {
        when(sizePriceService.updateSizePrice(eq(1L), any()))
                .thenThrow(new NoFieldsProvidedException("At least one field must be provided"));

        mockMvc.perform(put("/api/menu/size-prices/1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}
