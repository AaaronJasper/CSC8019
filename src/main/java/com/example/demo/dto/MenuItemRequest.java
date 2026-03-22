package com.example.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a menu item.
 * This is a class (not a record) so Spring can bind `multipart/form-data` from Postman.
 */
public class MenuItemRequest {

    @NotBlank(message = "name is required")
    @Size(max = 200, message = "name must be <= 200 characters")
    private String name;

    @NotBlank(message = "description is required")
    @Size(max = 2000, message = "description must be <= 2000 characters")
    private String description;

    @NotBlank(message = "imgUrl is required")
    @Size(max = 2048, message = "imgUrl must be <= 2048 characters")
    private String imgUrl;

    @NotNull(message = "rating is required")
    @DecimalMin(value = "0.0", message = "rating must be >= 0.0")
    @DecimalMax(value = "5.0", message = "rating must be <= 5.0")
    @Digits(integer = 1, fraction = 1, message = "rating must have at most 1 decimal place")
    private BigDecimal rating;

    @NotBlank(message = "category is required")
    @Size(max = 100, message = "category must be <= 100 characters")
    private String category;

    @NotNull(message = "itemCount is required")
    @Min(value = 0, message = "itemCount must be >= 0")
    private Integer itemCount;

    public MenuItemRequest() {}

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

