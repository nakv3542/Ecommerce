package com.example.forfinal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PayPalConfig {

    @Value("${paypal.client-id:YOUR_CLIENT_ID}")
    private String clientId;

    @Value("${paypal.secret:YOUR_SECRET}")
    private String secret;

    @Value("${paypal.mode:sandbox}")
    private String mode;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getClientId() { return clientId; }
    public String getSecret() { return secret; }

    public String getBaseUrl() {
        return "sandbox".equalsIgnoreCase(mode)
                ? "https://api-m.sandbox.paypal.com"
                : "https://api-m.paypal.com";
    }
}
