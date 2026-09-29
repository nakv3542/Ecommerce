package com.example.forfinal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.forfinal.dto.OrderResponse;
import com.example.forfinal.entity.Cart;
import com.example.forfinal.entity.CartItem;
import com.example.forfinal.entity.Coupon;
import com.example.forfinal.entity.Order;
import com.example.forfinal.entity.OrderItem;
import com.example.forfinal.entity.Product;
import com.example.forfinal.entity.User;
import com.example.forfinal.repository.CartRepository;
import com.example.forfinal.repository.OrderItemRepository;
import com.example.forfinal.repository.OrderRepository;
import com.example.forfinal.repository.ProductRepository;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private TelegramService telegramService;

    @Transactional
    public OrderResponse placeOrder(User user, Long addressId, String paymentMethod, String couponCode) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PLACED");
        order.setPaymentMethod(paymentMethod);
        order.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        if (addressId != null) {
            order.setShippingAddress(addressService.getById(addressId));
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double subtotal = 0;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            double itemPrice = product.getDiscountPrice() > 0 ? product.getDiscountPrice() : product.getPrice();
            orderItem.setPrice(itemPrice * cartItem.getQuantity());

            subtotal += orderItem.getPrice();
            orderItems.add(orderItem);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setItems(orderItems);
        order.setSubtotal(subtotal);

        double discount = 0;
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon coupon = couponService.validateAndGetEntity(couponCode, subtotal);
            discount = couponService.applyDiscount(coupon, subtotal);
            order.setCoupon(coupon);
            couponService.incrementUsage(coupon);
        }
        order.setDiscount(discount);

        double shippingCost = subtotal >= 100 ? 0 : 9.99;
        order.setShippingCost(shippingCost);
        order.setTotalPrice(subtotal - discount + shippingCost);

        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        cart.getItems().clear();
        cartRepository.save(cart);

        telegramService.sendOrderNotification(savedOrder, "New Order Placed");
        

        return new OrderResponse(savedOrder);


    }

    public Page<OrderResponse> getUserOrders(User user, int page, int size, String sortBy) {
        Sort sort = Sort.by("createdAt").descending();
        if (sortBy.equals("id")) sort = Sort.by("id").descending();
        else if (sortBy.equals("totalPrice")) sort = Sort.by("totalPrice").descending();
        return orderRepository.findByUser(user, PageRequest.of(page, size, sort))
                .map(OrderResponse::new);
    }

    public Page<OrderResponse> getAllOrders(int page, int size, String sortBy) {
        Sort sort = Sort.by("createdAt").descending();
        if (sortBy.equals("id")) sort = Sort.by("id").descending();
        else if (sortBy.equals("totalPrice")) sort = Sort.by("totalPrice").descending();
        return orderRepository.findAll(PageRequest.of(page, size, sort))
                .map(OrderResponse::new);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String previousStatus = order.getStatus();

        order.setStatus(status);
        if ("DELIVERED".equals(status)) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        if ("CANCELLED".equals(status) && !"CANCELLED".equals(previousStatus)) {
            restoreStock(order);
        }

        Order updated = orderRepository.save(order);
        telegramService.sendOrderNotification(updated, "Order Status Changed to " + status);
        return new OrderResponse(updated);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only cancel your own orders");
        }

        String status = order.getStatus();
        if (!"PLACED".equals(status) && !"PENDING".equals(status)) {
            throw new RuntimeException("Order can only be cancelled if it is in PLACED or PENDING status");
        }

        order.setStatus("CANCELLED");
        restoreStock(order);

        Order cancelled = orderRepository.save(order);
        telegramService.sendOrderNotification(cancelled, "Order Cancelled by Customer");
        return new OrderResponse(cancelled);
    }

    private void restoreStock(Order order) {
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product != null) {
                    product.setStock(product.getStock() + item.getQuantity());
                    productRepository.save(product);
                }
            }
        }
    }

    public OrderResponse getOrderById(Long id) {
        return new OrderResponse(orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found")));
    }
}
