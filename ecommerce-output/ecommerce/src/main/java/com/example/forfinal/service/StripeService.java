package com.example.forfinal.service;

import com.example.forfinal.config.StripeConfig;
import com.example.forfinal.entity.Cart;
import com.example.forfinal.entity.CartItem;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Autowired
    private StripeConfig stripeConfig;

    public StripePaymentResponse createPaymentIntent(Cart cart) throws StripeException {
        long amountCents = 0;
        for (CartItem item : cart.getItems()) {
            double price = item.getProduct().getDiscountPrice() > 0
                    ? item.getProduct().getDiscountPrice()
                    : item.getProduct().getPrice();
            amountCents += (long) (price * item.getQuantity() * 100);
        }

        long shippingCents = amountCents >= 10000 ? 0 : 999;
        long totalCents = amountCents + shippingCents;

        if (totalCents < 50) {
            totalCents = 50;
        }
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(totalCents)
                .setCurrency("usd")
                .addPaymentMethodType("card")
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        StripePaymentResponse response = new StripePaymentResponse();
        response.clientSecret = intent.getClientSecret();
        response.paymentIntentId = intent.getId();
        response.amount = totalCents;
        return response;
    }

    public static class StripePaymentResponse {
        private String clientSecret;
        private String paymentIntentId;
        private long amount;

        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
        public String getPaymentIntentId() { return paymentIntentId; }
        public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }
        public long getAmount() { return amount; }
        public void setAmount(long amount) { this.amount = amount; }
    }
}
