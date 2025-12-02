package com.graso.anitrack.anime.application.port.in;

import java.util.Optional;

import com.graso.anitrack.anime.domain.model.Anime;

public interface GetAnimeByNameUseCase {
    Optional<Anime> getByName(String name);
}
