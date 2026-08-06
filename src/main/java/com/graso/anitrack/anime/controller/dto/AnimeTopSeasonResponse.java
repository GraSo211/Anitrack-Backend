package com.graso.anitrack.anime.controller.dto;

import com.graso.anitrack.anime.model.AnimeTopSeason;
import com.graso.anitrack.anime.model.MediaTitle;

public record AnimeTopSeasonResponse(
        int id,
        MediaTitle title,
        String bannerImage,
        Integer averageScore,
        Integer popularity
) {
    public static AnimeTopSeasonResponse from(AnimeTopSeason topSeason) {
        if (topSeason == null) return null;
        return new AnimeTopSeasonResponse(
                topSeason.id(),
                topSeason.title(),
                topSeason.bannerImage(),
                topSeason.averageScore(),
                topSeason.popularity()
        );
    }
}
