package com.graso.anitrack.external.myanimelist.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ResponseAnimeToListRequest(
        String status,
        Integer score,
        Integer numEpisodesWatched,
        Boolean isRewatching,
        OffsetDateTime updatedAt,
        Integer priority,
        Integer numTimesRewatched,
        Integer rewatchValue,
        List<String> tags,
        String comments
) {
}
