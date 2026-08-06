package com.graso.anitrack.animelist.model;

public record AnimeItem(
        int id,
        String title,
        Picture mainPicture,
        Status status,
        Integer score,
        Integer episodesWatched,
        boolean isRewatching
) {
}
