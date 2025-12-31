package com.graso.anitrack.anime.application.service;

import com.graso.anitrack.anime.application.port.in.GetAnimeByIdUseCase;
import com.graso.anitrack.anime.application.port.out.AnimeQueryPort;
import com.graso.anitrack.anime.domain.model.Anime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnimeService implements GetAnimeByIdUseCase {
    AnimeQueryPort animeQueryPort;

    @Override
    public Anime getById(Long id) {
        return animeQueryPort.findById(id);
    }
}
