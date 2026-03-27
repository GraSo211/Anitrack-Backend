package com.graso.anitrack.animelist.application.in;

import com.graso.anitrack.animelist.domain.AnimeStatus;

public interface AddAnimeToListUseCase {
    AnimeStatus addAnimeToList(String token, int id);

    AnimeStatus modifyAnimeToList(String token, int id, String status, int score, int numEpisodes);
}
