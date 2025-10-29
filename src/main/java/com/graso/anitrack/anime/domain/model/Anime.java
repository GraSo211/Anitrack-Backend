package com.graso.anitrack.anime.domain.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public record Anime(
        Long id,
        Long malId,

        String name,
        String summary,
        String status,

        Optional<String> bannerImage,
        Optional<String> image,

        List<String> genres,

        Optional<Integer> rating,
        Optional<Integer> popularity,

        Optional<String> source,

        Optional<Integer> episodeCount,
        Optional<LocalDate> startDate,
        Optional<Integer> averageEpisodeDuration,

        boolean adult
) {
}
