package com.food.filter;

import com.food.config.BucketConfig;

import io.github.bucket4j.Bucket;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {

    private final ConcurrentHashMap<String, Bucket> cache =
            new ConcurrentHashMap<>();

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req =
                (HttpServletRequest) request;

        HttpServletResponse res =
                (HttpServletResponse) response;

        String path = req.getRequestURI();

        String key =
                req.getRemoteAddr() + path;

        Bucket bucket = null;

        // Signup OTP
        if (path.contains("/signup/mobile/send-otp")) {

            bucket = cache.computeIfAbsent(
                    key,
                    k -> BucketConfig.signupOtpBucket()
            );
        }

        // Login OTP
        else if (path.contains("/login/send-otp")) {

            bucket = cache.computeIfAbsent(
                    key,
                    k -> BucketConfig.loginOtpBucket()
            );
        }

        // Login API
        else if (path.equals("/api/auth/login")) {

            bucket = cache.computeIfAbsent(
                    key,
                    k -> BucketConfig.loginBucket()
            );
        }

        if (bucket != null) {

            if (bucket.tryConsume(1)) {

                chain.doFilter(request, response);

            } else {

                res.setStatus(429);

                res.getWriter().write(
                        "Too many requests. Try again later."
                );
            }

        } else {

            chain.doFilter(request, response);
        }
    }
}