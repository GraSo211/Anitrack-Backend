package com.graso.anitrack.anime.domain.model;

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
