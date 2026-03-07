package com.graso.anitrack.anime.domain.model.genres;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tag {
    private String name;
    private String description;
    private boolean isAdult;
}
