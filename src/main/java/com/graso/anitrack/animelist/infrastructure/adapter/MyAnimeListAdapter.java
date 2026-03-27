package com.graso.anitrack.animelist.infrastructure.adapter;

import com.graso.anitrack.animelist.application.out.AnimeListCommandPort;
import com.graso.anitrack.animelist.application.out.AnimeListQueryPort;
import com.graso.anitrack.animelist.domain.AnimeList;
import com.graso.anitrack.animelist.domain.AnimeStatus;
import com.graso.anitrack.external.myanimelist.MyAnimeListApiClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@AllArgsConstructor
public class MyAnimeListAdapter implements AnimeListQueryPort, AnimeListCommandPort {
    MyAnimeListApiClient myAnimeListApiClient;

    @Override
    public AnimeList findAnimeList(String token, String status) {
        return myAnimeListApiClient.getAnimeList(token, status);
    }

    @Override
    public AnimeStatus findAnimeListStatus(String token, int id) {
        return myAnimeListApiClient.getAnimeStatus(token, id);
    }


    @Override
    public AnimeStatus addAnimeToList(String token, int id) {
        return myAnimeListApiClient.addAnimeToList(token, id);
    }

    @Override
    public AnimeStatus modifyAnimeToList(String token, int id, String status, int score, int numEpisodes) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("status", status);
        body.add("score", String.valueOf(score));
        body.add("num_watched_episodes", String.valueOf(numEpisodes));

        return myAnimeListApiClient.modifyAnimeToList(token, id, body);
    }
}
