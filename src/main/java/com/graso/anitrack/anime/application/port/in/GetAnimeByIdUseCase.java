package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.domain.model.Anime;

public interface GetAnimeByIdUseCase {
    Anime getById(Long id);
}
