package com.graso.anitrack.animelist.controller.dto;

import com.graso.anitrack.animelist.model.AnimeStatus;

public record AnimeStatusResponse(
        String status,
        int score,
        int numEpisodes,
        boolean rewatching
) {
    public static AnimeStatusResponse from(AnimeStatus animeStatus) {
        return new AnimeStatusResponse(
                animeStatus.status(),
                animeStatus.score(),
                animeStatus.numEpisodes(),
                animeStatus.rewatching()
        );
    }
}
