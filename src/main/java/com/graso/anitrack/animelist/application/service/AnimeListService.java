package com.graso.anitrack.animelist.application.service;

import com.graso.anitrack.animelist.application.out.AnimeListCommandPort;
import com.graso.anitrack.animelist.application.out.AnimeListQueryPort;
import com.graso.anitrack.animelist.domain.AnimeList;
import com.graso.anitrack.animelist.domain.AnimeStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnimeListService {
    private final AnimeListQueryPort animeListQueryPort;
    private final AnimeListCommandPort animeListCommandPort;

    public AnimeList getAnimeListUseCase(String token, String status) {
        return animeListQueryPort.findAnimeList(token, status);
    }

    public AnimeStatus getAnimeListStatus(String token, int id) {
        return animeListQueryPort.findAnimeListStatus(token, id);
    }

    public AnimeStatus addAnimeToList(String token, int id) {
        return animeListCommandPort.addAnimeToList(token, id);
    }

    public AnimeStatus modifyAnimeToList(String token, int id, String status, int score, int numEpisodes) {
        return animeListCommandPort.modifyAnimeToList(token, id, status, score, numEpisodes);
    }
}
