package com.graso.anitrack.animelist.model;

public record AnimeStatus(
        String status,
        int score,
        int numEpisodes,
        boolean rewatching
) {
}
