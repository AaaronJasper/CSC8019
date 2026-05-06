package com.example.demo.dto;

import java.time.LocalTime;
import java.util.List;

public class PlaceOrderRequest {

    private Long customerId;//for showing the customer ID when looged in, guest can be null here
    private String guestName;
    private List<Long>menuItemIds;//list of id of drink requested
    private List<String>sizes;//list of each drink's sizes
    private List<Integer>quantities;//each drink's quantity list
    private LocalTime pickupTime; // optional, e.g. "14:30:00" — validated server-side

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public List<Long> getMenuItemIds() {
        return menuItemIds;
    }

    public void setMenuItemIds(List<Long> menuItemIds) {
        this.menuItemIds = menuItemIds;
    }

    public List<Integer> getQuantities() {
        return quantities;
    }

    public void setQuantities(List<Integer> quantities) {
        this.quantities = quantities;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public LocalTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalTime pickupTime) {
        this.pickupTime = pickupTime;
    }
}
