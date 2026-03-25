package com.graso.anitrack.animelist.application.out;

import com.graso.anitrack.animelist.domain.AnimeList;
import com.graso.anitrack.animelist.domain.AnimeStatus;

public interface AnimeListQueryPort {
    AnimeList findAnimeList(String token, String status);

    AnimeStatus findAnimeListStatus(String token, int id);

}
