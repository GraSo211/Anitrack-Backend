package com.graso.anitrack.anime.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnimeCard {
    int id;
    Integer idMal;
    MediaTitle title;
    MediaCoverImage coverImage; // imagen vertical

}
