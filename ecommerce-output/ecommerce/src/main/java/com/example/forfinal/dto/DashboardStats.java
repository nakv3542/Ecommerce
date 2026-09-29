package com.example.forfinal.dto;

import java.util.Map;

public class DashboardStats {
    private long totalProducts;
    private long totalOrders;
    private long totalUsers;
    private double totalRevenue;
    private double monthlyRevenue;
    private long pendingOrders;
    private Map<String, Long> ordersByStatus;
    private Map<String, Long> ordersByMonth;

    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public double getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(double monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
    public long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }
    public Map<String, Long> getOrdersByStatus() { return ordersByStatus; }
    public void setOrdersByStatus(Map<String, Long> ordersByStatus) { this.ordersByStatus = ordersByStatus; }
    public Map<String, Long> getOrdersByMonth() { return ordersByMonth; }
    public void setOrdersByMonth(Map<String, Long> ordersByMonth) { this.ordersByMonth = ordersByMonth; }
}
