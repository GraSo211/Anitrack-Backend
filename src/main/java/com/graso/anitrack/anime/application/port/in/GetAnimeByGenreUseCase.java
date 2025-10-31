package com.graso.anitrack.anime.application.port.in;

import java.util.List;

import com.graso.anitrack.anime.domain.model.Media;


public interface GetAnimeByGenreUseCase {
    List<Media> getByGenre(String genre);
}
