package com.example.forfinal.repository;

import com.example.forfinal.entity.Address;
import com.example.forfinal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}
