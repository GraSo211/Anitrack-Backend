package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.domain.genre.Genre;

import java.util.List;

public interface GetGenresUseCase {
    List<Genre> getGenres();
}
