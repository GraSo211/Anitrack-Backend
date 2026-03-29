package com.graso.anitrack.anime.application.port.in;

import com.graso.anitrack.anime.domain.anime.Anime;

public interface GetAnimeByIdUseCase {
    Anime getById(int id);

    Anime getByMalId(int id);
}
