package com.graso.anitrack.anime.infrastructure.anilist.adapter;

import com.graso.anitrack.anime.application.port.out.AnimeQueryPort;
import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.domain.model.AnimeName;
import com.graso.anitrack.anime.domain.model.AnimeReleasing;
import com.graso.anitrack.anime.domain.model.AnimeTopSeason;
import com.graso.anitrack.anime.infrastructure.anilist.client.AniListApiClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Primary
@Component
@AllArgsConstructor
public class AniListAnimeQueryAdapter implements AnimeQueryPort {
    private AniListApiClient aniListApiClient;

    @Override
    public Anime findById(Long id) {
        return aniListApiClient.fetchAnimeById(id);
    }

    @Override
    public Map<String, String> getBannerImage() {
        return aniListApiClient.fetchBannerImage();
    }

    @Override
    public AnimeTopSeason findTopSeasonAnime() {
        return aniListApiClient.findTopAnimeSeason();
    }

    @Override
    public List<AnimeName> findByName(String name) {
        return aniListApiClient.fetchAnimeByName(name);
    }

    @Override
    public List<AnimeReleasing> findAnimesReleasing() {
        return aniListApiClient.fetchReleasingAnimes();
    }


}
