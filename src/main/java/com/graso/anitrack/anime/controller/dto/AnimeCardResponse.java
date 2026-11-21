package com.graso.anitrack.anime.controller.dto;

import com.graso.anitrack.anime.model.AnimeCard;
import com.graso.anitrack.anime.model.MediaCoverImage;
import com.graso.anitrack.anime.model.MediaTitle;

import java.util.List;

public record AnimeCardResponse(
        int id,
        Integer idMal,
        MediaTitle title,
        MediaCoverImage coverImage,
        String format,
        Integer averageScore,
        List<String> genres,
        String description,
        String bannerImage
) {
    public static AnimeCardResponse from(AnimeCard card) {
        return new AnimeCardResponse(
                card.id(),
                card.idMal(),
                card.title(),
                card.coverImage(),
                card.format(),
                card.averageScore(),
                card.genres(),
                card.description(),
                card.bannerImage()
        );
    }
}
