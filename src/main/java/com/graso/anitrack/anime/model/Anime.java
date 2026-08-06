package com.graso.anitrack.anime.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
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
    private final Integer duration;
    private final String countryOfOrigin;
    private final MediaSource source;
    private final MediaTrailer trailer;
    private final MediaCoverImage coverImage;
    private final String bannerImage;
    private final List<String> genres;
    private final List<String> synonyms;
    private final Integer averageScore;
    private final Integer popularity;
    private MediaRelations relations;
    private final String studio;
    private final boolean isAdult;
    private final AiringSchedule nextAiringEpisode;

    public void keepOnlyAnimeRelations() {
        if (relations == null) return;
        this.relations = relations.onlyAnime();
    }
}
