package com.example.demo.dto;

import java.math.BigDecimal;

public class MenuItemSizePriceUpdateRequest {

    private String size;

    private BigDecimal price;

    public MenuItemSizePriceUpdateRequest() {}

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
