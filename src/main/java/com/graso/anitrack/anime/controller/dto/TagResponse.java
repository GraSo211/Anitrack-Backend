package com.graso.anitrack.anime.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graso.anitrack.anime.model.Tag;

public record TagResponse(
        String name,
        String description,
        @JsonProperty("isAdult") boolean isAdult
) {
    public static TagResponse from(Tag tag) {
        return new TagResponse(
                tag.name(),
                tag.description(),
                tag.isAdult()
        );
    }
}
