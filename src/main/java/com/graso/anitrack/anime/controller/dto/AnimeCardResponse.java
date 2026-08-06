package com.graso.anitrack.anime.controller.dto;

import com.graso.anitrack.anime.model.AnimeCard;
import com.graso.anitrack.anime.model.MediaCoverImage;
import com.graso.anitrack.anime.model.MediaTitle;

public record AnimeCardResponse(
        int id,
        Integer idMal,
        MediaTitle title,
        MediaCoverImage coverImage
) {
    public static AnimeCardResponse from(AnimeCard card) {
        return new AnimeCardResponse(
                card.id(),
                card.idMal(),
                card.title(),
                card.coverImage()
        );
    }
}
