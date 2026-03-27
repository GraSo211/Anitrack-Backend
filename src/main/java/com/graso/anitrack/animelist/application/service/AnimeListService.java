package com.graso.anitrack.animelist.application.service;

import com.graso.anitrack.animelist.application.in.AddAnimeToListUseCase;
import com.graso.anitrack.animelist.application.in.GetAnimeListStatusUseCase;
import com.graso.anitrack.animelist.application.in.GetAnimeListUseCase;
import com.graso.anitrack.animelist.application.out.AnimeListCommandPort;
import com.graso.anitrack.animelist.application.out.AnimeListQueryPort;
import com.graso.anitrack.animelist.domain.AnimeList;
import com.graso.anitrack.animelist.domain.AnimeStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnimeListService implements GetAnimeListUseCase, GetAnimeListStatusUseCase, AddAnimeToListUseCase {
    AnimeListQueryPort animeListQueryPort;
    AnimeListCommandPort animeListCommandPort;

    @Override
    public AnimeList getAnimeListUseCase(String token, String status) {
        return animeListQueryPort.findAnimeList(token, status);
    }

    @Override
    public AnimeStatus getAnimeListStatus(String token, int id) {
        return animeListQueryPort.findAnimeListStatus(token, id);
    }

    @Override
    public AnimeStatus addAnimeToList(String token, int id) {
        return animeListCommandPort.addAnimeToList(token, id);
    }

    @Override
    public AnimeStatus modifyAnimeToList(String token, int id, String status, int score, int numEpisodes) {
        return animeListCommandPort.modifyAnimeToList(token, id, status, score, numEpisodes);
    }
}
