package com.graso.anitrack.anime.controller.dto;

import com.graso.anitrack.anime.model.AiringSchedule;
import com.graso.anitrack.anime.model.AnimeReleasing;
import com.graso.anitrack.anime.model.MediaCoverImage;
import com.graso.anitrack.anime.model.MediaTitle;

public record AnimeReleasingResponse(
        int id,
        Integer idMal,
        MediaTitle title,
        MediaCoverImage coverImage,
        AiringSchedule nextAiringEpisode
) {
    public static AnimeReleasingResponse from(AnimeReleasing releasing) {
        return new AnimeReleasingResponse(
                releasing.id(),
                releasing.idMal(),
                releasing.title(),
                releasing.coverImage(),
                releasing.nextAiringEpisode()
        );
    }
}
