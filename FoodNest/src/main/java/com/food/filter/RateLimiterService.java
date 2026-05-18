package com.food.filter;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    public boolean isAllowed(String key, int limit, int windowSeconds) {

        String redisKey = "rate:" + key;

        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count == null) return false;

        if (count == 1) {
            redisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
        }

        return count <= limit;
    }
}