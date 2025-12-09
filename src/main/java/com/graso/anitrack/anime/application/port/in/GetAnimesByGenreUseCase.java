package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.domain.model.AnimeCard;

import java.util.List;

public interface GetAnimesByGenreUseCase {
    List<AnimeCard> getAnimesByGenre(String genre);
}
