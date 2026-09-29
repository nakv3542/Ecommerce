package com.example.forfinal.service;

import com.example.forfinal.dto.DashboardStats;
import com.example.forfinal.entity.Order;
import com.example.forfinal.repository.OrderRepository;
import com.example.forfinal.repository.ProductRepository;
import com.example.forfinal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalProducts(productRepository.count());
        stats.setTotalUsers(userRepository.count());

        List<Order> allOrders = orderRepository.findAll();
        stats.setTotalOrders(allOrders.size());

        double totalRevenue = allOrders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()))
                .mapToDouble(Order::getTotalPrice).sum();
        stats.setTotalRevenue(totalRevenue);

        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        double monthlyRevenue = allOrders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()) && o.getCreatedAt() != null && o.getCreatedAt().isAfter(monthStart))
                .mapToDouble(Order::getTotalPrice).sum();
        stats.setMonthlyRevenue(monthlyRevenue);

        long pendingOrders = allOrders.stream()
                .filter(o -> "PLACED".equals(o.getStatus()) || "PENDING".equals(o.getStatus()))
                .count();
        stats.setPendingOrders(pendingOrders);

        Map<String, Long> byStatus = allOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
        stats.setOrdersByStatus(byStatus);

        Map<String, Long> byMonth = new HashMap<>();
        for (Order o : allOrders) {
            if (o.getCreatedAt() != null) {
                String key = o.getCreatedAt().getYear() + "-" + String.format("%02d", o.getCreatedAt().getMonthValue());
                byMonth.merge(key, 1L, Long::sum);
            }
        }
        stats.setOrdersByMonth(byMonth);

        return stats;
    }
}
