package com.graso.anitrack.anime.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnimeName {
    private final int id;
    private final Integer malId;
    private final MediaTitle title;
    private final MediaCoverImage coverImage; // imagen vertical
}
