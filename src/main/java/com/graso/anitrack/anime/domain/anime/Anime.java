package com.graso.anitrack.anime.domain.anime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graso.anitrack.anime.domain.anime.valueobject.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Anime {

    private final int id;
    private final Integer malId;
    private final MediaTitle title;
    private final MediaStatus status;
    private final String description;
    private final FuzzyDate startDate;
    private final FuzzyDate endDate;
    private final MediaSeason season;
    private final Integer seasonYear;
    private final Integer episodes;
    private final Integer duration; // tiempo promedio de duración de los caps
    private final String countryOfOrigin; // ISO 3166-1 alpha-2
    private final MediaSource source;
    private final MediaTrailer trailer;
    private final MediaCoverImage coverImage; // imagen vertical
    private final String bannerImage; // imagen horizontal de fondo
    private final List<String> genres;
    private final List<String> synonyms;
    private final Integer averageScore;
    private final Integer popularity;
    private MediaRelations relations;
    private final String studio;
    @JsonProperty("isAdult")
    private final boolean isAdult;
    private final AiringSchedule nextAiringEpisode;


    public void keepOnlyAnimeRelations() {
        if (relations == null) return;
        this.relations = relations.onlyAnime();
    }
}