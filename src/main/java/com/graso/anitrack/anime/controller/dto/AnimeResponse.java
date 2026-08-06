package com.graso.anitrack.anime.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graso.anitrack.anime.model.*;

import java.util.List;

public record AnimeResponse(
        int id,
        Integer malId,
        MediaTitle title,
        MediaStatus status,
        String description,
        FuzzyDate startDate,
        FuzzyDate endDate,
        MediaSeason season,
        Integer seasonYear,
        Integer episodes,
        Integer duration,
        String countryOfOrigin,
        MediaSource source,
        MediaTrailer trailer,
        MediaCoverImage coverImage,
        String bannerImage,
        List<String> genres,
        List<String> synonyms,
        Integer averageScore,
        Integer popularity,
        MediaRelations relations,
        String studio,
        @JsonProperty("isAdult") boolean isAdult,
        AiringSchedule nextAiringEpisode
) {
    public static AnimeResponse from(Anime anime) {
        return new AnimeResponse(
                anime.getId(),
                anime.getMalId(),
                anime.getTitle(),
                anime.getStatus(),
                anime.getDescription(),
                anime.getStartDate(),
                anime.getEndDate(),
                anime.getSeason(),
                anime.getSeasonYear(),
                anime.getEpisodes(),
                anime.getDuration(),
                anime.getCountryOfOrigin(),
                anime.getSource(),
                anime.getTrailer(),
                anime.getCoverImage(),
                anime.getBannerImage(),
                anime.getGenres(),
                anime.getSynonyms(),
                anime.getAverageScore(),
                anime.getPopularity(),
                anime.getRelations(),
                anime.getStudio(),
                anime.isAdult(),
                anime.getNextAiringEpisode()
        );
    }
}
