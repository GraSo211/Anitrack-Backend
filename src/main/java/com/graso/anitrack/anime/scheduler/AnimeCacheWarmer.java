package com.graso.anitrack.anime.scheduler;

import com.graso.anitrack.anime.service.AnimeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class AnimeCacheWarmer {

    private final AnimeService animeService;

    @Scheduled(
            fixedDelayString = "${anitrack.cache.releasing.warmup-ms:1800000}",
            initialDelayString = "${anitrack.cache.releasing.warmup-initial-delay-ms:30000}"
    )
    public void warmReleasingAnimesCache() {
        try {
            log.info("Warming releasingAnimesCache");
            animeService.getReleasingAnimes();
        } catch (Exception e) {
            log.warn("Failed to warm releasingAnimesCache", e);
        }
    }
}
