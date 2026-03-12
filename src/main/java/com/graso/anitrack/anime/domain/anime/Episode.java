package com.graso.anitrack.anime.domain.anime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Episode {
    int malId;
    String title;
    boolean filler;
    boolean recap;

}
