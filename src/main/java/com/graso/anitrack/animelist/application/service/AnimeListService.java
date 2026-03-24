package com.graso.anitrack.animelist.application.service;

import com.graso.anitrack.animelist.application.in.GetAnimeListUseCase;
import com.graso.anitrack.animelist.application.out.AnimeListQueryPort;
import com.graso.anitrack.animelist.domain.AnimeList;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnimeListService implements GetAnimeListUseCase {
    AnimeListQueryPort animeListQueryPort;

    @Override
    public AnimeList getAnimeListUseCase(String token, String status) {
        return animeListQueryPort.findAnimeList(token, status);
    }
}
