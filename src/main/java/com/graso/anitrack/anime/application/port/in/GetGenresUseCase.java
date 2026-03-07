package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.domain.model.genres.Genre;

import java.util.List;

public interface GetGenresUseCase {
    List<Genre> getGenres();
}
