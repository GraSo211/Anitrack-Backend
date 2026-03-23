package com.graso.anitrack.animelist.infrastructure.adapter;

import com.graso.anitrack.animelist.application.out.AnimeListQueryPort;
import com.graso.anitrack.animelist.domain.AnimeList;
import com.graso.anitrack.external.myanimelist.MyAnimeListApiClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MyAnimeListAdapter implements AnimeListQueryPort {
    MyAnimeListApiClient myAnimeListApiClient;

    @Override
    public AnimeList findAnimeList(String token, String status) {
        return myAnimeListApiClient.getAnimeList(token, status);
    }

    
}
