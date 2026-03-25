package com.example.demo.exception;

public class MenuItemsNotFoundException extends RuntimeException {

    public MenuItemsNotFoundException() {
        super("No menu items found");
    }
}

