package com.graso.anitrack.animelist.application.in;

import com.graso.anitrack.animelist.domain.AnimeList;

public interface GetAnimeListUseCase {
    AnimeList getAnimeListUseCase(String token, String status);
}
