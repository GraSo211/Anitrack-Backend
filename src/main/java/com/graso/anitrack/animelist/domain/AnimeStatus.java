package com.graso.anitrack.animelist.domain;

public record AnimeStatus(
        String status,
        int score,
        int numEpisodes,
        boolean rewatching
) {
}
