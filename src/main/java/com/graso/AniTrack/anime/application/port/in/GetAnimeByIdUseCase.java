package com.graso.anitrack.anime.application.port.in;

import java.util.Optional;

import com.graso.anitrack.anime.domain.model.Anime;

public interface GetAnimeByIdUseCase {
    Optional<Anime> getById(Long id);
}
