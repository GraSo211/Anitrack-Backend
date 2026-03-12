package com.graso.anitrack.anime.application.dto;

import com.graso.anitrack.anime.domain.anime.AiringSchedule;
import com.graso.anitrack.anime.domain.anime.valueobject.MediaCoverImage;
import com.graso.anitrack.anime.domain.anime.valueobject.MediaTitle;
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
