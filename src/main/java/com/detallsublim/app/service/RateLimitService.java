package com.detallsublim.app.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private static final int MAX_TRACKED_KEYS = 20_000;

    private static final long CLEANUP_INTERVAL = 256;

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    private final AtomicLong operations = new AtomicLong();

    public Result consume(String key, int maxRequests, Duration windowDuration) {
        long now = System.currentTimeMillis();

        cleanupIfNecessary(now);

        /*
         * Protección adicional contra un intento
         * de llenar la memoria con miles de claves.
         */
        if (windows.size() >= MAX_TRACKED_KEYS && !windows.containsKey(key)) {
            cleanupExpired(now);

            if (windows.size() >= MAX_TRACKED_KEYS) {
                return new Result(false, Math.max(1, windowDuration.toSeconds()));
            }
        }

        AtomicReference<Result> result = new AtomicReference<>();

        windows.compute(key, (ignored, current) -> {
            if (current == null || now >= current.resetAtMillis()) {
                long resetAt = now + windowDuration.toMillis();

                result.set(new Result(true, 0));

                return new Window(1, resetAt);
            }

            if (current.count() >= maxRequests) {
                result.set(new Result(false, secondsUntil(current.resetAtMillis(), now)));

                return current;
            }

            result.set(new Result(true, 0));

            return new Window(current.count() + 1, current.resetAtMillis());
        });

        return result.get();
    }

    public void reset(String key) {
        windows.remove(key);
    }

    public String clientKey(String scope, HttpServletRequest request) {
        String remoteAddress = request.getRemoteAddr();

        if (remoteAddress == null || remoteAddress.isBlank()) {
            remoteAddress = "unknown";
        }

        return scope + ":" + remoteAddress;
    }

    private void cleanupIfNecessary(long now) {
        long currentOperation = operations.incrementAndGet();

        if (currentOperation % CLEANUP_INTERVAL == 0) {
            cleanupExpired(now);
        }
    }

    private void cleanupExpired(long now) {
        windows.entrySet().removeIf(entry -> now >= entry.getValue().resetAtMillis());
    }

    private static long secondsUntil(long resetAt, long now) {
        long millis = Math.max(0, resetAt - now);

        return Math.max(1, (millis + 999) / 1000);
    }

    private record Window(int count, long resetAtMillis) {}

    public record Result(boolean allowed, long retryAfterSeconds) {}
}
