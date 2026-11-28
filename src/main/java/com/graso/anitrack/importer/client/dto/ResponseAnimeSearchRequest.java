package com.graso.anitrack.importer.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ResponseAnimeSearchRequest(
        List<Data> data,
        Paging paging
) {

    public record Data(Node node) {
    }

    public record Node(
            int id,
            String title,
            @JsonProperty("main_picture")
            Picture mainPicture,
            @JsonProperty("alternative_titles")
            AlternativeTitles alternativeTitles,
            @JsonProperty("num_episodes")
            Integer numEpisodes,
            @JsonProperty("media_type")
            String mediaType,
            @JsonProperty("start_date")
            String startDate
    ) {
        public record Picture(String medium, String large) {
        }

        public record AlternativeTitles(String en, String ja, List<String> synonyms) {
        }
    }

    public record Paging(String next) {
    }
}
