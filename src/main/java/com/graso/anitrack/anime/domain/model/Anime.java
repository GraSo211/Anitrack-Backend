package com.graso.anitrack.anime.domain.model;

import java.time.LocalDate;
import java.util.List;

public record Anime(
        Long id,
        Long malId,
        String name,
        String summary,
        String status,
        String bannerImage,
        int rating,
        int popularity,
        List<String> genres,
        String image,
        String source,
        int episodeCount,
        LocalDate startDate,
        int averageEpisodeDuration
) {
}
