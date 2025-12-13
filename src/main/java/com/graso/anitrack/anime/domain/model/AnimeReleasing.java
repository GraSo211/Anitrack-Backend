package com.graso.anitrack.anime.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnimeReleasing {
    int id;
    Integer idMal;
    MediaTitle title;
    MediaCoverImage coverImage;
    AiringSchedule nextAiringEpisode;
}
