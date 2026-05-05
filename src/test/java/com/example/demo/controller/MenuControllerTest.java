package com.example.demo.controller;

import com.example.demo.config.JWTAuthenticationFilter;
import com.example.demo.config.SecurityConfiguration;
import com.example.demo.entity.MenuItem;
import com.example.demo.exception.MenuItemNotFoundException;
import com.example.demo.exception.MenuItemsNotFoundException;
import com.example.demo.exception.NoFieldsProvidedException;
import com.example.demo.service.MenuService;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MenuController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, ServletWebSecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfiguration.class, JWTAuthenticationFilter.class}
        )
)
class MenuControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean MenuService menuService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private MenuItem sampleItem(String name) {
        return new MenuItem(name, "A great drink", "http://img.url",
                new BigDecimal("4.5"), true, "drinks", 10);
    }

    // ── GET /api/menu ─────────────────────────────────────────────────────────

    @Test
    void getAllMenuItems_returns200WithList() throws Exception {
        when(menuService.getAllMenuItems()).thenReturn(List.of(sampleItem("Latte"), sampleItem("Espresso")));

        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Latte")))
                .andExpect(jsonPath("$[1].name", is("Espresso")));
    }

    @Test
    void getAllMenuItems_emptyMenu_returns404() throws Exception {
        when(menuService.getAllMenuItems()).thenThrow(new MenuItemsNotFoundException());

        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    // ── GET /api/menu/{id} ────────────────────────────────────────────────────

    @Test
    void getMenuItemById_found_returns200() throws Exception {
        when(menuService.getMenuItemById(1L)).thenReturn(sampleItem("Latte"));

        mockMvc.perform(get("/api/menu/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Latte")))
                .andExpect(jsonPath("$.category", is("drinks")));
    }

    @Test
    void getMenuItemById_notFound_returns404() throws Exception {
        when(menuService.getMenuItemById(99L)).thenThrow(new MenuItemNotFoundException(99L));

        mockMvc.perform(get("/api/menu/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    // ── POST /api/menu ────────────────────────────────────────────────────────

    @Test
    void addMenuItem_validRequest_returns201() throws Exception {
        when(menuService.addMenuItem(any())).thenReturn(sampleItem("Latte"));

        mockMvc.perform(multipart("/api/menu")
                        .param("name", "Latte")
                        .param("description", "A great drink")
                        .param("imgUrl", "http://img.url")
                        .param("rating", "4.5")
                        .param("category", "drinks")
                        .param("itemCount", "10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Latte")));
    }

    @Test
    void addMenuItem_missingName_returns400() throws Exception {
        mockMvc.perform(multipart("/api/menu")
                        .param("description", "A great drink")
                        .param("imgUrl", "http://img.url")
                        .param("rating", "4.5")
                        .param("category", "drinks")
                        .param("itemCount", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addMenuItem_missingDescription_returns400() throws Exception {
        mockMvc.perform(multipart("/api/menu")
                        .param("name", "Latte")
                        .param("imgUrl", "http://img.url")
                        .param("rating", "4.5")
                        .param("category", "drinks")
                        .param("itemCount", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addMenuItem_ratingAboveMax_returns400() throws Exception {
        mockMvc.perform(multipart("/api/menu")
                        .param("name", "Latte")
                        .param("description", "desc")
                        .param("imgUrl", "http://img.url")
                        .param("rating", "6.0")
                        .param("category", "drinks")
                        .param("itemCount", "10"))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/menu/{id} ────────────────────────────────────────────────────

    @Test
    void updateMenuItem_validRequest_returns200() throws Exception {
        when(menuService.updateMenuItem(eq(1L), any())).thenReturn(sampleItem("Updated Latte"));

        mockMvc.perform(put("/api/menu/1")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("name", "Updated Latte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Latte")));
    }

    @Test
    void updateMenuItem_notFound_returns404() throws Exception {
        when(menuService.updateMenuItem(eq(99L), any())).thenThrow(new MenuItemNotFoundException(99L));

        mockMvc.perform(put("/api/menu/99")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("name", "Something"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMenuItem_noFieldsProvided_returns400() throws Exception {
        when(menuService.updateMenuItem(eq(1L), any()))
                .thenThrow(new NoFieldsProvidedException("At least one field must be provided"));

        mockMvc.perform(put("/api/menu/1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /api/menu/{id} ─────────────────────────────────────────────────

    @Test
    void softDeleteMenuItem_returns200WithMessage() throws Exception {
        when(menuService.softDeleteMenuItem(1L)).thenReturn("MenuItem with id 1 has been soft-deleted (isAvailable=false)");

        mockMvc.perform(delete("/api/menu/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("soft-deleted")));
    }

    @Test
    void softDeleteMenuItem_notFound_returns404() throws Exception {
        when(menuService.softDeleteMenuItem(99L)).thenThrow(new MenuItemNotFoundException(99L));

        mockMvc.perform(delete("/api/menu/99"))
                .andExpect(status().isNotFound());
    }
}
