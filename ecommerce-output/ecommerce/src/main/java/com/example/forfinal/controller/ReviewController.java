package com.example.forfinal.controller;

import com.example.forfinal.dto.ReviewRequest;
import com.example.forfinal.dto.ReviewResponse;
import com.example.forfinal.entity.User;
import com.example.forfinal.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public List<ReviewResponse> getProductReviews(@PathVariable Long productId) {
        return reviewService.getProductReviews(productId);
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<ReviewResponse> addReview(@AuthenticationPrincipal User user,
                                                     @PathVariable Long productId,
                                                     @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(user, productId, request));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Object getAllReviews(@RequestParam(required = false) Integer page,
                                @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return reviewService.getAllReviewsPaged(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        }
        return reviewService.getAllReviews();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
