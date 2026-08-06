package com.graso.anitrack.anime.model;

public record AnimeCard(
        int id,
        Integer idMal,
        MediaTitle title,
        MediaCoverImage coverImage
) {
}
