package com.food.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.food.dto.RateLimitRule;
import com.food.security.CustomUserPrincipal;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final RateLimiterService rateLimiterService;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        // Skip static / health
        if (path.startsWith("/actuator") || path.contains("/public")) {
            chain.doFilter(request, response);
            return;
        }

        String userKey = getUserKey();
        if (userKey == null) {
            userKey = req.getRemoteAddr();
        }

        String key = userKey + ":" + normalize(path);

        RateLimitRule rule = getRule(path);

        if (!rateLimiterService.isAllowed(key, rule.limit(), rule.windowSeconds())) {

            res.setStatus(429);
            res.getWriter().write("Too many requests. Please try again later.");
            return;
        }

        chain.doFilter(request, response);
    }

    // ================= RULES =================
    private RateLimitRule getRule(String path) {

        // OTP Signup
        if (path.contains("/signup/otp")) {
            return new RateLimitRule(5, 600); // 10 min
        }

        // OTP Login
        if (path.contains("/login/otp")) {
            return new RateLimitRule(5, 600); // 10 min
        }

        // Login API
        if (path.contains("/login")) {
            return new RateLimitRule(10, 600); // 10 min
        }

        // Dashboard
        if (path.contains("/dashboard")) {
            return new RateLimitRule(60, 60); // 1 min
        }

        // Toggle status
        if (path.contains("/toggle-status")) {
            return new RateLimitRule(20, 60); // 1 min
        }

        return new RateLimitRule(100, 60);
    }

    // ================= USER KEY =================
    private String getUserKey() {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.getPrincipal() instanceof CustomUserPrincipal user) {
                return user.getUserId().toString();
            }

        } catch (Exception e) {
            return null;
        }

        return null;
    }

    // ================= PATH NORMALIZER =================
    private String normalize(String path) {
        return path.replaceAll("[0-9]+", "{id}");
    }
}