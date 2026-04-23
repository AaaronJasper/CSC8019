package com.example.demo.dto;

import com.example.demo.entity.OrderStatus;

public class UpdateStatusRequest {
    private OrderStatus newStatus;

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }
}
