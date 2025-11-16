package com.graso.anitrack.anime.application.service;

import com.graso.anitrack.anime.application.port.in.GetAnimeByIdUseCase;
import com.graso.anitrack.anime.application.port.in.GetHomepageBannerAnimeUseCase;
import com.graso.anitrack.anime.application.port.in.GetTopSeasonAnimeUseCase;
import com.graso.anitrack.anime.application.port.out.AnimeQueryPort;
import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.domain.model.AnimeTopSeason;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class AnimeService implements GetAnimeByIdUseCase, GetHomepageBannerAnimeUseCase, GetTopSeasonAnimeUseCase {
    AnimeQueryPort animeQueryPort;

    @Override
    public Anime getById(Long id) {

        Anime anime = animeQueryPort.findById(id);
        anime.keepOnlyAnimeRelations();
        return anime;
    }

    @Override
    public Map<String, String> getBanner() {
        return animeQueryPort.getBannerImage();
    }


    @Override
    public AnimeTopSeason getTopSeasonAnime() {
        return animeQueryPort.findTopSeasonAnime();
    }
}
