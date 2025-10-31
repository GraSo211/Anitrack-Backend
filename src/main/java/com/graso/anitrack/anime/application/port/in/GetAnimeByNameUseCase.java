package com.graso.anitrack.anime.application.port.in;

import java.util.Optional;

import com.graso.anitrack.anime.domain.model.Media;

public interface GetAnimeByNameUseCase {
    Optional<Media> getByName(String name);
}
