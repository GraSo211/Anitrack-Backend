package com.graso.anitrack.anime.model;

public record AnimeTopSeason(
        int id,
        MediaTitle title,
        String bannerImage,
        Integer averageScore,
        Integer popularity
) {
}
