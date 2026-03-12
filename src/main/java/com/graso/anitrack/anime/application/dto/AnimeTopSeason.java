package com.graso.anitrack.anime.application.dto;

import com.graso.anitrack.anime.domain.anime.valueobject.MediaTitle;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AnimeTopSeason {
    private final int id;
    private final MediaTitle title;
    private final String bannerImage; // imagen horizontal de fondo
    private final Integer averageScore;
    private final Integer popularity;
}
