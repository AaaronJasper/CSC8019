package com.example.demo.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")

public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)//every item will belong to an order
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;//identifies the actual product

    @Column(nullable = false, length = 10)
    private String size;//size of the beverage(regular/large)

    @Column(nullable = false)
    private int quantity;//quantity of an item

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal unitPrice;//one unit's price

    /*
    multiply() used to caculate bigdecimal items oneunitprice of an item multiplied by quantity
     */
    public BigDecimal getLineTotal(){
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    //constructor

    public OrderItem(Long id, Order order, MenuItem menuItem, String size, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.order = order;
        this.menuItem = menuItem;
        this.size = size;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public OrderItem(Order order, MenuItem menuItem, String size, int quantity, BigDecimal unitPrice) {
        this.order = order;
        this.menuItem = menuItem;
        this.size = size;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    //getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
