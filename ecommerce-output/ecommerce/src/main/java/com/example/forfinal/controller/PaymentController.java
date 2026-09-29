package com.example.forfinal.controller;

import com.example.forfinal.config.StripeConfig;
import com.example.forfinal.dto.ErrorResponse;
import com.example.forfinal.dto.OrderResponse;
import com.example.forfinal.entity.Cart;
import com.example.forfinal.entity.CartItem;
import com.example.forfinal.entity.User;
import com.example.forfinal.repository.CartRepository;
import com.example.forfinal.service.BakongService;
import com.example.forfinal.service.PayPalService;
import com.example.forfinal.service.OrderService;
import com.example.forfinal.service.StripeService;
import com.example.forfinal.service.StripeService.StripePaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private BakongService bakongService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private StripeConfig stripeConfig;

    @GetMapping("/stripe-publishable-key")
    public ResponseEntity<?> getStripePublishableKey() {
        return ResponseEntity.ok(Map.of("publishableKey", stripeConfig.getPublishableKey()));
    }

    @PostMapping("/create-stripe-payment-intent")
    public ResponseEntity<?> createStripePaymentIntent(@AuthenticationPrincipal User user) {
        try {
            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Cart is empty"));
            StripePaymentResponse response = stripeService.createPaymentIntent(cart);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create payment: " + e.getMessage()));
        }
    }

    @PostMapping("/create-paypal-order")
    public ResponseEntity<?> createPayPalOrder(@AuthenticationPrincipal User user) {
        try {
            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Cart is empty"));

            double subtotal = 0;
            for (CartItem item : cart.getItems()) {
                double price = item.getProduct().getDiscountPrice() > 0
                        ? item.getProduct().getDiscountPrice() : item.getProduct().getPrice();
                subtotal += price * item.getQuantity();
            }
            double shipping = subtotal >= 100 ? 0 : 9.99;
            double total = subtotal + shipping;

            Map<String, Object> paypalOrder = payPalService.createOrder(
                    total, "USD",
                    "http://localhost:8080/payments/success",
                    "http://localhost:5173/checkout"
            );
            return ResponseEntity.ok(paypalOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create PayPal order: " + e.getMessage()));
        }
    }

    @PostMapping("/capture-paypal-order")
    public ResponseEntity<?> capturePayPalOrder(@AuthenticationPrincipal User user,
                                                  @RequestParam String paypalOrderId,
                                                  @RequestParam(required = false) Long addressId,
                                                  @RequestParam(defaultValue = "PAYPAL") String paymentMethod,
                                                  @RequestParam(required = false) String couponCode) {
        try {
            Map<String, Object> capture = payPalService.captureOrder(paypalOrderId);
            String status = (String) capture.get("status");
            if ("COMPLETED".equals(status)) {
                OrderResponse order = orderService.placeOrder(user, addressId, "PAYPAL", couponCode);
                return ResponseEntity.ok(Map.of("status", "COMPLETED", "order", order));
            }
            return ResponseEntity.badRequest().body(Map.of("status", status, "message", "Payment not completed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to capture order: " + e.getMessage()));
        }
    }

    @PostMapping("/create-bakong-qr")
    public ResponseEntity<?> createBakongQR(@AuthenticationPrincipal User user) {
        try {
            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Cart is empty"));

            double subtotal = 0;
            for (CartItem item : cart.getItems()) {
                double price = item.getProduct().getDiscountPrice() > 0
                        ? item.getProduct().getDiscountPrice() : item.getProduct().getPrice();
                subtotal += price * item.getQuantity();
            }
            double shipping = subtotal >= 100 ? 0 : 9.99;
            double total = subtotal + shipping;

            String khqrPayload = bakongService.generateKHQRPayload(total);
            String qrBase64 = bakongService.generateQRCodeImage(khqrPayload, 300, 300);

            return ResponseEntity.ok(Map.of(
                "qrCode", "data:image/png;base64," + qrBase64,
                "amount", total,
                "merchantName", "My Store"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create Bakong QR: " + e.getMessage()));
        }
    }
}
