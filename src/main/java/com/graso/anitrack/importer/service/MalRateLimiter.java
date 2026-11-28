package com.graso.anitrack.importer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Serializa las llamadas a MAL de todas las importaciones: al menos 1s entre requests.
 * MAL no publica headers de rate limit y castiga con 403/504 genéricos, así que el
 * throttle bloquea globalmente unos minutos.
 */
@Component
public class MalRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(MalRateLimiter.class);
    private static final long MIN_INTERVAL_MS = 1000;
    private static final long THROTTLE_MS = 3 * 60 * 1000;

    private final long minIntervalMs;
    private final long throttleMs;
    private long nextAllowedAt = 0;
    private long blockedUntil = 0;

    public MalRateLimiter() {
        this(MIN_INTERVAL_MS, THROTTLE_MS);
    }

    MalRateLimiter(long minIntervalMs, long throttleMs) {
        this.minIntervalMs = minIntervalMs;
        this.throttleMs = throttleMs;
    }

    public void acquire() {
        long wait;
        synchronized (this) {
            long now = System.currentTimeMillis();
            long target = Math.max(nextAllowedAt, blockedUntil);
            wait = target - now;
            nextAllowedAt = Math.max(target, now) + minIntervalMs;
        }
        if (wait > 0) {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Importación interrumpida");
            }
        }
    }

    public void throttle(String reason) {
        long until;
        synchronized (this) {
            blockedUntil = System.currentTimeMillis() + throttleMs;
            until = blockedUntil;
        }
        log.warn("MAL throttle ({}): pausando llamadas hasta {}", reason, until);
    }
}
