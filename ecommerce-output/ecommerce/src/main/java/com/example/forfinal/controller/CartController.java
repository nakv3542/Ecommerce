package com.example.forfinal.controller;

import com.example.forfinal.entity.Cart;
import com.example.forfinal.entity.User;
import com.example.forfinal.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> viewCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@AuthenticationPrincipal User user,
                                          @RequestParam Long productId,
                                          @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.addToCart(user, productId, quantity));
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<Cart> updateQuantity(@AuthenticationPrincipal User user,
                                                @PathVariable Long itemId,
                                                @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(user, itemId, quantity));
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<Cart> removeFromCart(@AuthenticationPrincipal User user,
                                                @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeFromCart(user, itemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.ok(Map.of("message", "Cart cleared"));
    }
}
