package com.example.demo.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("No order found with id: " + id);
    }
}
