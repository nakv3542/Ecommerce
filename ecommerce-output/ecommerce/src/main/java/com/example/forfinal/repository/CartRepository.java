package com.example.forfinal.repository;

import com.example.forfinal.entity.Cart;
import com.example.forfinal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
