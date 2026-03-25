package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.MenuItemSizePriceRequest;
import com.example.demo.dto.MenuItemSizePriceUpdateRequest;
import com.example.demo.entity.MenuItemSizePrice;
import com.example.demo.service.MenuItemSizePriceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/menu")
public class MenuItemSizePriceController {

    private final MenuItemSizePriceService sizePriceService;

    public MenuItemSizePriceController(MenuItemSizePriceService sizePriceService) {
        this.sizePriceService = sizePriceService;
    }

    @PostMapping(value = "/size-prices", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<MenuItemSizePrice> createSizePrice(@Valid @ModelAttribute MenuItemSizePriceRequest request) {
        MenuItemSizePrice created = sizePriceService.createSizePrice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/size-prices/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<MenuItemSizePrice> updateSizePrice(
            @PathVariable Long id,
            @Valid @ModelAttribute MenuItemSizePriceUpdateRequest request
    ) {
        return ResponseEntity.ok(sizePriceService.updateSizePrice(id, request));
    }
}
