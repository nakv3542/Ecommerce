package com.example.forfinal.repository;

import com.example.forfinal.entity.Order;
import com.example.forfinal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);
    @NonNull
    Page<Order> findAll(Pageable pageable);
}
