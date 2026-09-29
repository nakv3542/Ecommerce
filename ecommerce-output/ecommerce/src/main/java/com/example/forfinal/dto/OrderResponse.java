package com.example.forfinal.dto;

import com.example.forfinal.entity.Order;
import com.example.forfinal.entity.OrderItem;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponse {
    private Long id;
    private String userName;
    private String userEmail;
    private double subtotal;
    private double discount;
    private double shippingCost;
    private double totalPrice;
    private String status;
    private String paymentMethod;
    private String trackingNumber;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private AddressResponse shippingAddress;
    private String couponCode;
    private List<OrderItemResponse> items;

    public OrderResponse() {}

    public OrderResponse(Order o) {
        this.id = o.getId();
        this.userName = o.getUser().getName();
        this.userEmail = o.getUser().getEmail();
        this.subtotal = o.getSubtotal();
        this.discount = o.getDiscount();
        this.shippingCost = o.getShippingCost();
        this.totalPrice = o.getTotalPrice();
        this.status = o.getStatus();
        this.paymentMethod = o.getPaymentMethod();
        this.trackingNumber = o.getTrackingNumber();
        this.createdAt = o.getCreatedAt();
        this.deliveredAt = o.getDeliveredAt();
        if (o.getShippingAddress() != null) this.shippingAddress = new AddressResponse(o.getShippingAddress());
        if (o.getCoupon() != null) this.couponCode = o.getCoupon().getCode();
        if (o.getItems() != null) {
            this.items = o.getItems().stream().map(OrderItemResponse::new).collect(Collectors.toList());
        }
    }

    public Long getId() { return id; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public double getSubtotal() { return subtotal; }
    public double getDiscount() { return discount; }
    public double getShippingCost() { return shippingCost; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getTrackingNumber() { return trackingNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public AddressResponse getShippingAddress() { return shippingAddress; }
    public String getCouponCode() { return couponCode; }
    public List<OrderItemResponse> getItems() { return items; }

    public static class OrderItemResponse {
        private Long id;
        private String productName;
        private Long productId;
        private String productImage;
        private int quantity;
        private double price;

        public OrderItemResponse() {}

        public OrderItemResponse(OrderItem oi) {
            this.id = oi.getId();
            this.productName = oi.getProduct().getName();
            this.productId = oi.getProduct().getId();
            this.productImage = oi.getProduct().getImages() != null && !oi.getProduct().getImages().isEmpty()
                ? oi.getProduct().getImages().get(0).getImageUrl()
                : "https://picsum.photos/seed/" + oi.getProduct().getName().replaceAll("\\s+", "") + "/400/400";
            this.quantity = oi.getQuantity();
            this.price = oi.getPrice();
        }

        public Long getId() { return id; }
        public String getProductName() { return productName; }
        public Long getProductId() { return productId; }
        public String getProductImage() { return productImage; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
    }
}
