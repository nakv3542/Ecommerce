package com.example.forfinal.service;

import com.example.forfinal.dto.CouponRequest;
import com.example.forfinal.dto.CouponResponse;
import com.example.forfinal.entity.Coupon;
import com.example.forfinal.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public List<CouponResponse> getAll() {
        return couponRepository.findAll().stream().map(CouponResponse::new).collect(Collectors.toList());
    }

    public Page<CouponResponse> getAllPaged(Pageable pageable) {
        return couponRepository.findAll(pageable).map(CouponResponse::new);
    }

    public CouponResponse getById(Long id) {
        return new CouponResponse(couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found")));
    }

    public CouponResponse create(CouponRequest req) {
        Coupon coupon = new Coupon();
        applyRequest(coupon, req);
        return new CouponResponse(couponRepository.save(coupon));
    }

    public CouponResponse update(Long id, CouponRequest req) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        applyRequest(coupon, req);
        return new CouponResponse(couponRepository.save(coupon));
    }

    public void delete(Long id) {
        couponRepository.deleteById(id);
    }

    public Coupon validateAndGetEntity(String code, double orderTotal) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));
        if (!coupon.isActive()) throw new RuntimeException("Coupon is not active");
        if (coupon.getCurrentUsage() >= coupon.getMaxUsage()) throw new RuntimeException("Coupon usage limit reached");
        if (coupon.getValidFrom() != null && LocalDateTime.now().isBefore(coupon.getValidFrom()))
            throw new RuntimeException("Coupon is not yet valid");
        if (coupon.getValidUntil() != null && LocalDateTime.now().isAfter(coupon.getValidUntil()))
            throw new RuntimeException("Coupon has expired");
        if (orderTotal < coupon.getMinOrderAmount())
            throw new RuntimeException("Minimum order amount of $" + coupon.getMinOrderAmount() + " not met");
        return coupon;
    }

    public CouponResponse validate(String code, double orderTotal) {
        return new CouponResponse(validateAndGetEntity(code, orderTotal));
    }

    public double applyDiscount(Coupon coupon, double orderTotal) {
        if ("PERCENTAGE".equals(coupon.getType())) {
            return orderTotal * (coupon.getValue() / 100);
        } else {
            return Math.min(coupon.getValue(), orderTotal);
        }
    }

    public void incrementUsage(Coupon coupon) {
        coupon.setCurrentUsage(coupon.getCurrentUsage() + 1);
        if (coupon.getCurrentUsage() >= coupon.getMaxUsage()) {
            coupon.setActive(false);
        }
        couponRepository.save(coupon);
    }

    private void applyRequest(Coupon coupon, CouponRequest req) {
        coupon.setCode(req.getCode());
        coupon.setType(req.getType());
        coupon.setValue(req.getValue());
        coupon.setMinOrderAmount(req.getMinOrderAmount());
        coupon.setMaxUsage(req.getMaxUsage());
        coupon.setActive(req.isActive());
        coupon.setValidFrom(req.getValidFrom());
        coupon.setValidUntil(req.getValidUntil());
    }
}
