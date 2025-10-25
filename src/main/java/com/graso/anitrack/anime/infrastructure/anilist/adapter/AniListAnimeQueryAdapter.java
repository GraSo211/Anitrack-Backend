package com.graso.anitrack.anime.infrastructure.anilist.adapter;

import com.graso.anitrack.anime.application.port.out.AnimeQueryPort;
import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.infrastructure.anilist.client.AniListApiClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public Anime findByName(String name) {
        return null;
    }

    @Override
    public List<Anime> findByGenre(String genre) {
        return List.of();
    }

    @Override
    public List<Anime> findPopular(int limit) {
        return List.of();
    }

    @Override
    public List<Anime> findUpcoming(int limit) {
        return List.of();
    }
}
