package com.graso.anitrack.anime.domain.model.genres;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tag {
    private String name;
    private String description;
    @JsonProperty("isAdult")
    private boolean isAdult;
}
