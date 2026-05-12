package com.food.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import java.time.Duration;

public class BucketConfig {

    public static Bucket signupOtpBucket() {

        Bandwidth limit = Bandwidth.classic(
                5,
                Refill.intervally(
                        5,
                        Duration.ofMinutes(10)
                )
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public static Bucket loginOtpBucket() {

        Bandwidth limit = Bandwidth.classic(
                5,
                Refill.intervally(
                        5,
                        Duration.ofMinutes(10)
                )
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public static Bucket loginBucket() {

        Bandwidth limit = Bandwidth.classic(
                10,
                Refill.intervally(
                        10,
                        Duration.ofMinutes(10)
                )
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}