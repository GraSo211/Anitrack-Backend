package com.graso.anitrack.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class OAuthStateStore {

    private final Cache<String, Boolean> stateCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public void store(String state) {
        stateCache.put(state, true);
    }

    public boolean consume(String state) {
        Boolean present = stateCache.getIfPresent(state);
        if (present != null) {
            stateCache.invalidate(state);
            return true;
        }
        return false;
    }
}
