package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.dto.MenuItemRequest;
import com.example.demo.dto.MenuItemUpdateRequest;
import com.example.demo.entity.MenuItem;
import com.example.demo.service.MenuService;
import jakarta.validation.Valid;

@RestController
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // ===== GET /api/menu =====
    @GetMapping("/api/menu")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return ResponseEntity.ok(menuService.getAllMenuItems());
    }

    // ===== GET /api/menu/{id} =====
    @GetMapping("/api/menu/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenuItemById(id));
    }

    // ===== POST /api/menu =====
    @PostMapping(value = "/api/menu", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public ResponseEntity<MenuItem> addMenuItem(@Valid @ModelAttribute MenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.addMenuItem(request));
    }

    // ===== PUT /api/menu/{id} =====
    @PutMapping(
            value = "/api/menu/{id}",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE }
    )
    public ResponseEntity<?> updateMenuItem(
            @PathVariable Long id,
            @Valid @ModelAttribute MenuItemUpdateRequest request
    ) {
        return ResponseEntity.ok(menuService.updateMenuItem(id, request));
    }

    // ===== DELETE /api/menu/{id} ===== (soft delete)
    @DeleteMapping("/api/menu/{id}")
    public ResponseEntity<String> softDeleteMenuItem(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.softDeleteMenuItem(id));
    }
}
