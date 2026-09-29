package com.example.forfinal.service;

import com.example.forfinal.dto.WishlistResponse;
import com.example.forfinal.entity.Product;
import com.example.forfinal.entity.User;
import com.example.forfinal.entity.WishlistItem;
import com.example.forfinal.repository.ProductRepository;
import com.example.forfinal.repository.WishlistItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<WishlistResponse> getUserWishlist(User user) {
        return wishlistItemRepository.findByUser(user).stream()
                .map(WishlistResponse::new).collect(Collectors.toList());
    }

    public WishlistResponse addToWishlist(User user, Long productId) {
        if (wishlistItemRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new RuntimeException("Product already in wishlist");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setProduct(product);
        return new WishlistResponse(wishlistItemRepository.save(item));
    }

    @Transactional
    public void removeFromWishlist(User user, Long productId) {
        wishlistItemRepository.deleteByUserIdAndProductId(user.getId(), productId);
    }

    public boolean isInWishlist(User user, Long productId) {
        return wishlistItemRepository.existsByUserIdAndProductId(user.getId(), productId);
    }
}
