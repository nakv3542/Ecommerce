package com.example.forfinal.dto;

import com.example.forfinal.entity.Review;
import java.time.LocalDateTime;

public class ReviewResponse {
    private Long id;
    private int rating;
    private String comment;
    private String userName;
    private Long userId;
    private Long productId;
    private String productName;
    private LocalDateTime createdAt;

    public ReviewResponse() {}

    public ReviewResponse(Review r) {
        this.id = r.getId();
        this.rating = r.getRating();
        this.comment = r.getComment();
        this.userName = r.getUser().getName();
        this.userId = r.getUser().getId();
        this.productId = r.getProduct().getId();
        this.productName = r.getProduct().getName();
        this.createdAt = r.getCreatedAt();
    }

    public Long getId() { return id; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getUserName() { return userName; }
    public Long getUserId() { return userId; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
