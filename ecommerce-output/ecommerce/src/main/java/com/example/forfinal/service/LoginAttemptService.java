package com.example.forfinal.service;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);
    private static final int MAX_IP_ATTEMPTS = 10;
    private static final long IP_BLOCK_DURATION_MINUTES = 15;

    private final Map<String, AtomicInteger> ipAttempts = new ConcurrentHashMap<>();
    private final Map<String, Instant> ipBlockedUntil = new ConcurrentHashMap<>();

    public String getClientIP(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) {
            return xri.trim();
        }
        return request.getRemoteAddr();
    }

    public boolean isIpBlocked(String ip) {
        Instant blockedUntil = ipBlockedUntil.get(ip);
        if (blockedUntil != null) {
            if (Instant.now().isBefore(blockedUntil)) {
                return true;
            }
            ipBlockedUntil.remove(ip);
            ipAttempts.remove(ip);
        }
        return false;
    }

    public void registerFailedAttempt(String ip) {
        AtomicInteger attempts = ipAttempts.computeIfAbsent(ip, k -> new AtomicInteger(0));
        int count = attempts.incrementAndGet();
        log.warn("Failed login attempt from IP: {} (attempt {}/{})", ip, count, MAX_IP_ATTEMPTS);
        if (count >= MAX_IP_ATTEMPTS) {
            ipBlockedUntil.put(ip, Instant.now().plusSeconds(IP_BLOCK_DURATION_MINUTES * 60));
            log.warn("IP {} blocked for {} minutes due to excessive failed attempts", ip, IP_BLOCK_DURATION_MINUTES);
        }
    }

    public void registerSuccessfulAttempt(String ip) {
        ipAttempts.remove(ip);
        ipBlockedUntil.remove(ip);
    }
}
