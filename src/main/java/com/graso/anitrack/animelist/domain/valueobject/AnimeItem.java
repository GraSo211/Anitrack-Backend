package com.graso.anitrack.animelist.domain.valueobject;

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