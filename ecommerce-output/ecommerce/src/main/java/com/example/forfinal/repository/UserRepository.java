package com.example.forfinal.repository;

import com.example.forfinal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE User u SET u.role = 'ADMIN', u.password = ?2 WHERE u.email = ?1")
    int fixAdminRole(String email, String hashedPassword);
}