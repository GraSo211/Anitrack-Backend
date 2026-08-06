package com.graso.anitrack.anime.client.anilist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ResponseGenresAniListDto(
        Data data
) {

    public record Data(
            @JsonProperty(value = "GenreCollection", required = true)
            List<String> genreCollection
    ) {

    }
}
