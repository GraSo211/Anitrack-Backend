package com.graso.anitrack.anime.application.port.in;

import java.util.List;

import com.graso.anitrack.anime.domain.model.Anime;


public interface GetAnimeByGenreUseCase {
    List<Anime> getByGenre(String genre);
}
