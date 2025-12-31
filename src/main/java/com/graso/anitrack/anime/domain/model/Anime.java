package com.graso.anitrack.anime.domain.model;

import java.util.Date;

public record Anime(
        Long id,
        Long malId,
        String name,
        String sumary,
        String status,
        int calification,
        int popularity,
        String[] genres,
        String image,
        String banner,
        String source,
        int epCount,
        Date startDate,
        int avgEpDuration


) {
}
