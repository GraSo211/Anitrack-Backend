package com.graso.anitrack.animelist.application.in;

import com.graso.anitrack.animelist.domain.AnimeStatus;

public interface GetAnimeListStatusUseCase {
    AnimeStatus getAnimeListStatus(String token, int id);

}
