package com.example.forfinal.service;

import com.example.forfinal.config.PayPalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class PayPalService {

    @Autowired
    private PayPalConfig payPalConfig;

    @Autowired
    private RestTemplate restTemplate;

    public String getAccessToken() {
        String auth = payPalConfig.getClientId() + ":" + payPalConfig.getSecret();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedAuth);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                payPalConfig.getBaseUrl() + "/v1/oauth2/token",
                request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    public Map<String, Object> createOrder(double total, String currency, String returnUrl, String cancelUrl) {
        String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        Map<String, Object> orderRequest = Map.of(
            "intent", "CAPTURE",
            "purchase_units", List.of(Map.of(
                "amount", Map.of(
                    "currency_code", currency,
                    "value", String.format("%.2f", total)
                )
            )),
            "payment_source", Map.of(
                "paypal", Map.of(
                    "experience_context", Map.of(
                        "return_url", returnUrl,
                        "cancel_url", cancelUrl
                    )
                )
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                payPalConfig.getBaseUrl() + "/v2/checkout/orders",
                request, Map.class);

        return response.getBody();
    }

    public Map<String, Object> captureOrder(String paypalOrderId) {
        String token = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                payPalConfig.getBaseUrl() + "/v2/checkout/orders/" + paypalOrderId + "/capture",
                request, Map.class);

        return response.getBody();
    }
}
