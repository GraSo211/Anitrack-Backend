package com.graso.anitrack.anime.client.anilist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graso.anitrack.anime.model.MediaType;

import java.util.List;

public record ResponseFetchByIdAniListDto(Data data) {

    public record Data(
            @JsonProperty("Media") Media media
    ) {

        public record Media(
                int id,
                Integer idMal,

                MediaType type,
                Title title,

                String description,

                CoverImage coverImage,
                String bannerImage,

                List<String> genres,
                List<String> synonyms,

                String status,
                String source,

                Integer episodes,
                Integer duration,
                Integer seasonYear,
                String season,

                StartDate startDate,
                EndDate endDate,

                Integer averageScore,
                Integer popularity,

                Boolean isAdult,

                Studios studios,
                NextAiringEpisode nextAiringEpisode,

                Relations relations,
                Trailer trailer
        ) {
        }

        public record Title(
                String romaji,
                String english
        ) {
        }

        public record CoverImage(
                String extraLarge,
                String large,
                String medium,
                String color
        ) {
        }

        public record StartDate(
                Integer year,
                Integer month,
                Integer day
        ) {
        }

        public record EndDate(
                Integer year,
                Integer month,
                Integer day
        ) {
        }

        public record Trailer(
                String id,
                String site,
                String thumbnail
        ) {
        }

        public record Studios(
                List<StudioEdge> edges
        ) {
        }

        public record StudioEdge(
                boolean isMain,
                Studio node
        ) {
        }

        public record Studio(
                Long id,
                String name
        ) {
        }

        public record Relations(
                List<RelationEdge> edges
        ) {
        }

        public record RelationEdge(
                String relationType,
                Media node
        ) {
        }

        public record NextAiringEpisode(
                Integer id,
                Integer airingAt,
                Integer timeUntilAiring,
                Integer episode,
                Integer mediaId
        ) {
        }
    }
}
