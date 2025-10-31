package com.graso.anitrack.anime.domain.model;

import java.util.List;

public record Media(
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
        Integer duration, //tiempo promedio de duracion de los caps
        String countryOfOrigin, // devuelve un codigo (ISO 3166-1 alpha-2)
        MediaSource source,
        MediaTrailer trailer,
        MediaCoverImage coverImage, // imagen vertical
        String bannerImage, // imagen de horizontal de fondo
        List<String> genres,
        List<String> synonyms,
        Integer averageScore,
        Integer popularity,
        MediaRelations relations,
        //todo: CharacterConnection characters
        String studio,
        boolean isAdult,
        AiringSchedule nextAiringEpisode


) {
    public Media {
        genres = genres != null ? genres : List.of();
        synonyms = synonyms != null ? synonyms : List.of();
    }
}
