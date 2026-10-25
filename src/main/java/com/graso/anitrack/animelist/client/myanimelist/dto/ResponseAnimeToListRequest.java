package com.graso.anitrack.animelist.client.myanimelist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record ResponseAnimeToListRequest(
        String status,
        Integer score,
        @JsonProperty("num_episodes_watched")
        Integer numEpisodesWatched,
        @JsonProperty("is_rewatching")
        Boolean isRewatching,
        OffsetDateTime updatedAt,
        Integer priority,
        Integer numTimesRewatched,
        Integer rewatchValue,
        List<String> tags,
        String comments
) {
}
