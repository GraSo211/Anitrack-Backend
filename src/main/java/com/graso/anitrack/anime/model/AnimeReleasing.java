package com.graso.anitrack.anime.model;

public record AnimeReleasing(
        int id,
        Integer idMal,
        MediaTitle title,
        MediaCoverImage coverImage,
        AiringSchedule nextAiringEpisode
) {
}
