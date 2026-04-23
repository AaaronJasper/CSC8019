package com.example.demo.entity;
/*
I have added a seperate columns for the small and large price. Please add it in the constructor and connect it with the array is required, although I think its not required now:))
I'll implement the fields in the service layer with the if-else condition

###important#### IsAvailable field requires a method. 
Something like in the morning a staff can add the available quntity once, and then as
customers keep buying the drink the number gets subtracted (total minus buyed quantity)

###important### rating field also reuires a method
we need to add a method where customers can give a rating and the system can show an average of all
the ratings got
*/
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "img_url")
    private String imgUrl;

    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "is_available")
    private Boolean isAvailable;

    private String category;

    @Column(name = "item_count")
    private Integer itemCount;

    @Column(nullable = true, precision = 8, scale = 2)
    private BigDecimal regularPrice;

    @Column(nullable = true, precision = 8, scale = 2)
    private BigDecimal largePrice;

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItemSizePrice> sizePrices = new ArrayList<>();

    public MenuItem() {}

    public MenuItem(String name, String description, String imgUrl,
                    BigDecimal rating, Boolean isAvailable,
                    String category, Integer itemCount) {
        this.name = name;
        this.description = description;
        this.imgUrl = imgUrl;
        this.rating = rating;
        this.isAvailable = isAvailable;
        this.category = category;
        this.itemCount = itemCount;
    }

    // ===== Getter / Setter =====

    public Long getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public List<MenuItemSizePrice> getSizePrices() {
        return sizePrices;
    }

    public void setSizePrices(List<MenuItemSizePrice> sizePrices) {
        this.sizePrices = sizePrices;
    }

    public MenuItemSizePrice getRegularPrice() {
        return sizePrices.stream()
                .filter(sp -> "regular".equalsIgnoreCase(sp.getSize()))
                .findFirst()
                .orElse(null);
    }

    public void setRegularPrice(BigDecimal regularPrice) {
        this.regularPrice = regularPrice;
    }

    public MenuItemSizePrice getLargePrice() {
        return sizePrices.stream()
                .filter(sp -> "large".equalsIgnoreCase(sp.getSize()))
                .findFirst()
                .orElse(null);
    }

    public void setLargePrice(BigDecimal largePrice) {
        this.largePrice = largePrice;
    }
}
