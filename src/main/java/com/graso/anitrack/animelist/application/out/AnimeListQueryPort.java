package com.graso.anitrack.animelist.application.out;

import com.graso.anitrack.animelist.domain.AnimeList;

public interface AnimeListQueryPort {
    AnimeList findAnimeList(String token, String status);
}
