package com.example.demo.exception;

public class MenuItemSizePriceNotFoundException extends RuntimeException {

    public MenuItemSizePriceNotFoundException(Long id) {
        super("No menu item size-price found with id: " + id);
    }
}
