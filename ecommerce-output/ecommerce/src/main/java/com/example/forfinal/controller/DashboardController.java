package com.example.forfinal.controller;

import com.example.forfinal.dto.DashboardStats;
import com.example.forfinal.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.forfinal.entity.User;
import com.example.forfinal.repository.UserRepository;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard/stats")
    public DashboardStats getStats() {
        return dashboardService.getStats();
    }

    @GetMapping("/users")
    public Page<User> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "15") int size) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by("id")));
    }
}
