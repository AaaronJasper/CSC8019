package com.example.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a menu item via PUT.
 * Separate from {@code MenuItemRequest} so rules can differ.
 */
public class MenuItemUpdateRequest {

    @Pattern(regexp = "\\S.*", message = "name must not be blank")
    @Size(max = 200, message = "name must be <= 200 characters")
    private String name;

    @Pattern(regexp = "\\S.*", message = "description must not be blank")
    @Size(max = 2000, message = "description must be <= 2000 characters")
    private String description;

    @Pattern(regexp = "\\S.*", message = "imgUrl must not be blank")
    @Size(max = 2048, message = "imgUrl must be <= 2048 characters")
    private String imgUrl;

    @DecimalMin(value = "0.0", message = "rating must be >= 0.0")
    @DecimalMax(value = "5.0", message = "rating must be <= 5.0")
    @Digits(integer = 1, fraction = 1, message = "rating must have at most 1 decimal place")
    private BigDecimal rating;

    @Pattern(regexp = "\\S.*", message = "category must not be blank")
    @Size(max = 50, message = "category must be <= 50 characters")
    private String category;

    // Optional field: only validated when provided (non-null)
    @Min(value = 1, message = "itemCount must be >= 1")
    private Integer itemCount;

    public MenuItemUpdateRequest() {}

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
}

