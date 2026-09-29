package com.example.forfinal.dto;

import com.example.forfinal.entity.WishlistItem;
import java.time.LocalDateTime;

public class WishlistResponse {
    private Long id;
    private Long productId;
    private String productName;
    private double productPrice;
    private Double discountPrice;
    private String productImage;
    private double averageRating;
    private int stock;
    private LocalDateTime addedAt;

    public WishlistResponse() {}

    public WishlistResponse(WishlistItem wi) {
        this.id = wi.getId();
        this.productId = wi.getProduct().getId();
        this.productName = wi.getProduct().getName();
        this.productPrice = wi.getProduct().getPrice();
        this.discountPrice = wi.getProduct().getDiscountPrice() > 0 ? wi.getProduct().getDiscountPrice() : null;
        this.productImage = wi.getProduct().getImages() != null && !wi.getProduct().getImages().isEmpty()
            ? wi.getProduct().getImages().get(0).getImageUrl()
            : "https://picsum.photos/seed/" + wi.getProduct().getName().replaceAll("\\s+", "") + "/400/400";
        this.averageRating = wi.getProduct().getAverageRating();
        this.stock = wi.getProduct().getStock();
        this.addedAt = wi.getAddedAt();
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getProductPrice() { return productPrice; }
    public Double getDiscountPrice() { return discountPrice; }
    public String getProductImage() { return productImage; }
    public double getAverageRating() { return averageRating; }
    public int getStock() { return stock; }
    public LocalDateTime getAddedAt() { return addedAt; }
}
