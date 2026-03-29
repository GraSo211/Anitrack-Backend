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

                // 1 hora
                buildCache("releasingAnimesCache", 1, TimeUnit.HOURS, 1000),

                // 1 día
                buildCache("seasonTrendCache", 1, TimeUnit.DAYS, 1000),

                // 7 días
                buildCache("topSeasonCache", 7, TimeUnit.DAYS, 100),
                buildCache("bannerImageCache", 7, TimeUnit.DAYS, 50),
                buildCache("mostValoratedCache", 7, TimeUnit.DAYS, 500),
                buildCache("upcomingReleasesCache", 7, TimeUnit.DAYS, 500),
                buildCache("genreAnimeCache", 7, TimeUnit.DAYS, 1000)

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
