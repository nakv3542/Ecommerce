package com.example.forfinal.service;

import com.example.forfinal.dto.ReviewRequest;
import com.example.forfinal.dto.ReviewResponse;
import com.example.forfinal.entity.Product;
import com.example.forfinal.entity.Review;
import com.example.forfinal.entity.User;
import com.example.forfinal.repository.ProductRepository;
import com.example.forfinal.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    public ReviewResponse addReview(User user, Long productId, ReviewRequest req) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Review review = new Review();
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setProduct(product);
        review.setUser(user);
        Review saved = reviewRepository.save(review);
        updateProductRating(product);
        return new ReviewResponse(saved);
    }

    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(ReviewResponse::new).collect(Collectors.toList());
    }

    public Page<ReviewResponse> getAllReviewsPaged(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(ReviewResponse::new);
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        Product product = review.getProduct();
        reviewRepository.delete(review);
        updateProductRating(product);
    }

    public List<ReviewResponse> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId).stream()
                .map(ReviewResponse::new).collect(Collectors.toList());
    }

    private void updateProductRating(Product product) {
        List<Review> reviews = reviewRepository.findByProductId(product.getId());
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        product.setAverageRating(Math.round(avg * 10.0) / 10.0);
        product.setReviewCount(reviews.size());
        productRepository.save(product);
    }
}
