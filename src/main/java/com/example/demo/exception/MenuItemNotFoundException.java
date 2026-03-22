package com.example.demo.exception;

public class MenuItemNotFoundException extends RuntimeException {

    public MenuItemNotFoundException(Long id) {
        super("No menu item found with id: " + id);
    }
}

