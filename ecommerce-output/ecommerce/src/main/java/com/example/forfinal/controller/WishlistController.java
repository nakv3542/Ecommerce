package com.example.forfinal.controller;

import com.example.forfinal.dto.WishlistResponse;
import com.example.forfinal.entity.User;
import com.example.forfinal.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public List<WishlistResponse> getWishlist(@AuthenticationPrincipal User user) {
        return wishlistService.getUserWishlist(user);
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<WishlistResponse> addToWishlist(@AuthenticationPrincipal User user,
                                                           @PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.addToWishlist(user, productId));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromWishlist(@AuthenticationPrincipal User user,
                                                 @PathVariable Long productId) {
        wishlistService.removeFromWishlist(user, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkInWishlist(@AuthenticationPrincipal User user,
                                                                  @PathVariable Long productId) {
        return ResponseEntity.ok(Map.of("inWishlist", wishlistService.isInWishlist(user, productId)));
    }
}
