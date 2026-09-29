package com.example.forfinal.service;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.forfinal.entity.Order;
import com.example.forfinal.entity.OrderItem;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class TelegramService {

    private static final Logger log = LoggerFactory.getLogger(TelegramService.class);
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot%s/sendMessage";
    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");

    private final String botToken;
    private final String chatId;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TelegramService(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.chat-id}") String chatId,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.botToken = botToken;
        this.chatId = chatId;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

public String sendOrderNotification(Order order, String event) {
    try {
        String message = formatOrderMessage(order, event);
        String url = String.format(TELEGRAM_API_URL, botToken);

        // ADD THESE DEBUG LINES
        log.info("DEBUG - botToken is null: {}", botToken == null);
        log.info("DEBUG - botToken is empty: {}", botToken != null && botToken.isEmpty());
        log.info("DEBUG - chatId: {}", chatId);
        log.info("DEBUG - url: {}", url);
        log.info("DEBUG - message: {}", message);

        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("chat_id", chatId);
        bodyMap.put("text", message);
        bodyMap.put("parse_mode", "Markdown");

        String jsonBody = objectMapper.writeValueAsString(bodyMap);
        log.info("DEBUG - jsonBody: {}", jsonBody); // ADD THIS

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class); // CAPTURE RESPONSE
        log.info("DEBUG - Telegram response status: {}", response.getStatusCode()); // ADD THIS
        log.info("DEBUG - Telegram response body: {}", response.getBody()); // ADD THIS

    } catch (Exception e) {
        log.error("Failed to send Telegram notification for order {}: {}", order.getId(), e.getMessage(), e); // ADD e as last arg
    }
    return event;
}

    private String formatOrderMessage(Order order, String event) {
        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDED2 *New Order Notification*");
        sb.append("\n\uD83D\uDD35 *Event:* ").append(event);
        sb.append("\n\n\uD83D\uDCCB *Order #").append(order.getId()).append("*");
        sb.append("\n\uD83D\uDC64 *Customer:* ").append(order.getUser().getName());
        sb.append("\n\u2709\uFE0F *Email:* ").append(order.getUser().getEmail());
        sb.append("\n\uD83D\uDCB5 *Total:* $").append(DF.format(order.getTotalPrice()));
        sb.append("\n\uD83D\uDCB3 *Payment:* ").append(order.getPaymentMethod());
        sb.append("\n\uD83D\uDCE6 *Status:* ").append(order.getStatus());
        sb.append("\n\uD83D\uDD0D *Tracking:* ").append(order.getTrackingNumber());

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            sb.append("\n\n\uD83D\uDCE6 *Items:*");
            for (OrderItem item : order.getItems()) {
                String productName = item.getProduct() != null ? item.getProduct().getName() : "N/A";
                sb.append("\n  \u2022 ").append(productName)
                  .append(" x").append(item.getQuantity())
                  .append(" ($").append(DF.format(item.getPrice())).append(")");
            }
        }

        sb.append("\n\n\uD83D\uDD52 ").append(java.time.LocalDateTime.now());
        return sb.toString();
    }

}
