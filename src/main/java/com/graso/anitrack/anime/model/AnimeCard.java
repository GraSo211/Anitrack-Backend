package com.graso.anitrack.anime.model;

import java.util.List;

public record AnimeCard(
        int id,
        Integer idMal,
        MediaTitle title,
        MediaCoverImage coverImage,
        String format,
        Integer averageScore,
        List<String> genres,
        String description,
        String bannerImage
) {
}
