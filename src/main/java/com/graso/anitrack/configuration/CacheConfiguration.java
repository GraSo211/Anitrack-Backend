package com.graso.anitrack.configuration;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {


        SimpleCacheManager manager = new SimpleCacheManager();
        List<CaffeineCache> caches = List.of(
                buildCache("animeHour", 1, TimeUnit.HOURS, 1000),
                buildCache("animeDay", 1, TimeUnit.DAYS, 1000),
                buildCache("animeWeek", 7, TimeUnit.DAYS, 1000)

        );
        manager.setCaches(caches);
        return manager;
    }


    private static CaffeineCache buildCache(String name, long ttl, TimeUnit timeUnit, long size) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(ttl, timeUnit)
                .maximumSize(size)
                .build());
    }

}
