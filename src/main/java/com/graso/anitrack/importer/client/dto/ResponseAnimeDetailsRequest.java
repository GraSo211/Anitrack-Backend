package com.graso.anitrack.importer.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ResponseAnimeDetailsRequest(
        int id,
        String title,
        @JsonProperty("main_picture")
        Picture mainPicture,
        @JsonProperty("num_episodes")
        Integer numEpisodes,
        @JsonProperty("media_type")
        String mediaType,
        @JsonProperty("start_date")
        String startDate,
        @JsonProperty("alternative_titles")
        AlternativeTitles alternativeTitles,
        @JsonProperty("related_anime")
        List<Related> relatedAnime
) {

    public record Picture(String medium, String large) {
    }

    public record AlternativeTitles(String en, String ja, List<String> synonyms) {
    }

    public record Related(
            Node node,
            @JsonProperty("relation_type")
            String relationType
    ) {
        public record Node(
                int id,
                String title,
                @JsonProperty("main_picture")
                Picture mainPicture
        ) {
        }
    }
}
