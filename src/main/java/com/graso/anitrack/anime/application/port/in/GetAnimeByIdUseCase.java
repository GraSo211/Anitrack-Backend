package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.domain.model.Media;

public interface GetAnimeByIdUseCase {
    Media getById(Long id);
}
