package com.example.demo.entity;

import jakarta.persistence.*;
//import org.apache.catalina.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;

@Entity
@Table(name = "orders")


public class Order {
    @Id//helps gives a uniquely identifiable number to each order
    @GeneratedValue(strategy = GenerationType.IDENTITY)//helps automatically assign the unque id to AN ORDER using identity column
    private Long id;

    public Order() {}

    /**
     * many to one because many orders can refer a single customer(meaning one customer record)
     * fetchtype lazy is used to save memory and time by only fetching the required details(in this case customer) when needed
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonBackReference("user-orders")
    private User customer;

    @Column(length = 100)
    private String guestName;

    @Enumerated(EnumType.STRING)//this is uses the categories defined in the OrderStatus Enum
    @Column(nullable = false)//because every order has to have a status
    private OrderStatus status;

    /*
    BigDecimal to use the exact amount of the total order price,
    The number can be 6 digit before and 2 digit after the decimal
     */
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal totalPrice;

    /*
    To define the exact time when the order was placed with no scope of updating later
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime orderTime;


    @Column
    private LocalTime pickupTime;


    @Column(nullable = false)
    private boolean advanceOrder = false;

    /*
   The field to define the loyalty free coffee scheme status and therefore to identify if the
   order uses loyalty coupon for the coffee in which case loyaltyRedeemed turns true
    */
    @Column(nullable = false)
    private boolean loyaltyRedeemed = false;

    @Column(nullable = false)
    private boolean archived = false;

    @Column(nullable = false)
    private LocalDateTime lastStatusChange;
    /**
     * one order has many items
     * everything in the order is deleted when the order is deleted (cascade)
     * if an item is deleted from the order, it gets deleted from the database too(orphanRemoval)
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();//this adds and stores items in an order

    /*
    Every order by default should have the new status
     */
    @PrePersist//to run the method before order is saved to the database, to keep the status new informed
    protected void onCreate(){
        orderTime = LocalDateTime.now();
        lastStatusChange = LocalDateTime.now();
        if(status == null){
            status = OrderStatus.NEW;
        }
    }

    /*
    To count all the items in the order.
    items.stream will go through every item in the order one by one, maptoint will note the quntity number
    of from each specific item and then we sum it
     */
    public int getItemCount(){
       return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    //constructors

    public Order(User customer, String guestName, OrderStatus status, BigDecimal totalPrice, boolean loyaltyRedeemed) {
        this.customer = customer;
        this.guestName = guestName;
        this.status = status;
        this.totalPrice = totalPrice;
        this.loyaltyRedeemed = loyaltyRedeemed;
    }

   /*blic Order(Long id, User customer, String guestName, OrderStatus status, BigDecimal totalPrice, LocalDateTime orderTime, boolean advanceOrder, LocalTime pickupTime, boolean loyaltyRedeemed, LocalDateTime lastStatusChange, List<OrderItem> items) {
        this.id = id;
        this.customer = customer;
        this.guestName = guestName;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderTime = orderTime;
        this.advanceOrder = advanceOrder;
        this.pickupTime = pickupTime;
        this.loyaltyRedeemed = loyaltyRedeemed;
        this.lastStatusChange = lastStatusChange;
        this.items = items;
    }*/


    //getters and setters

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public LocalTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public boolean isAdvanceOrder() {
        return advanceOrder;
    }

    public void setAdvanceOrder(boolean advanceOrder) {
        this.advanceOrder = advanceOrder;
    }

    public LocalDateTime getLastStatusChange() {
        return lastStatusChange;
    }

    public void setLastStatusChange(LocalDateTime lastStatusChange) {
        this.lastStatusChange = lastStatusChange;
    }

    public boolean isLoyaltyRedeemed() {
        return loyaltyRedeemed;
    }

    public void setLoyaltyRedeemed(boolean loyaltyRedeemed) {
        this.loyaltyRedeemed = loyaltyRedeemed;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
