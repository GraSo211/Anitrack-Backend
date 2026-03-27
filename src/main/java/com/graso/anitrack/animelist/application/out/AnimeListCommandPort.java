package com.graso.anitrack.animelist.application.out;

import com.graso.anitrack.animelist.domain.AnimeStatus;

public interface AnimeListCommandPort {
    AnimeStatus addAnimeToList(String token, int id);

    AnimeStatus modifyAnimeToList(String token, int id, String status, int score, int numEpisodes);
}
