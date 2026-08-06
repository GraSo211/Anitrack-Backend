package com.graso.anitrack.anime.client.anilist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ResponseTagsAniListDto(
        Data data
) {

    public record Data(
            @JsonProperty(value = "MediaTagCollection", required = true)
            List<MediaTagCollection> mediaTagCollection
    ) {
        public record MediaTagCollection(
                String name,
                String description,
                boolean isAdult
        ) {

        }
    }
}
