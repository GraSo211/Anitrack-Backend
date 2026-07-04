package com.graso.anitrack.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class OAuthStateStore {

    private final Cache<String, StateData> stateCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public record StateData(String randomState, String codeVerifier) {}

    public void store(String encodedState, String randomState, String codeVerifier) {
        stateCache.put(encodedState, new StateData(randomState, codeVerifier));
    }

    public StateData consume(String encodedState) {
        StateData data = stateCache.getIfPresent(encodedState);
        if (data != null) {
            stateCache.invalidate(encodedState);
        }
        return data;
    }
}
