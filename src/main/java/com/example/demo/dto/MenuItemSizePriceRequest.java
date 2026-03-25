package com.example.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MenuItemSizePriceRequest {

    @NotNull(message = "menuItemId is required")
    private Long menuItemId;

    @NotBlank(message = "size is required")
    private String size;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.00", message = "price must be >= 0.00")
    @Digits(integer = 8, fraction = 2, message = "price must have at most 2 decimal places")
    @DecimalMax(value = "1000.00", message = "price must be <= 1000.00")
    private BigDecimal price;

    public MenuItemSizePriceRequest() {}

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
