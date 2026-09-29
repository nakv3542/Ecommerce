package com.example.forfinal.dto;

import com.example.forfinal.entity.Coupon;
import java.time.LocalDateTime;

public class CouponResponse {
    private Long id;
    private String code;
    private String type;
    private double value;
    private double minOrderAmount;
    private int maxUsage;
    private int currentUsage;
    private boolean active;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    public CouponResponse() {}

    public CouponResponse(Coupon c) {
        this.id = c.getId();
        this.code = c.getCode();
        this.type = c.getType();
        this.value = c.getValue();
        this.minOrderAmount = c.getMinOrderAmount();
        this.maxUsage = c.getMaxUsage();
        this.currentUsage = c.getCurrentUsage();
        this.active = c.isActive();
        this.validFrom = c.getValidFrom();
        this.validUntil = c.getValidUntil();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getType() { return type; }
    public double getValue() { return value; }
    public double getMinOrderAmount() { return minOrderAmount; }
    public int getMaxUsage() { return maxUsage; }
    public int getCurrentUsage() { return currentUsage; }
    public boolean isActive() { return active; }
    public LocalDateTime getValidFrom() { return validFrom; }
    public LocalDateTime getValidUntil() { return validUntil; }
}
