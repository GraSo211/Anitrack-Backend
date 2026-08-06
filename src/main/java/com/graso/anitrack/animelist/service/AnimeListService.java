package com.graso.anitrack.animelist.service;

import com.graso.anitrack.animelist.client.myanimelist.MyAnimeListAnimeListClient;
import com.graso.anitrack.animelist.model.AnimeList;
import com.graso.anitrack.animelist.model.AnimeStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnimeListService {
    private final MyAnimeListAnimeListClient myAnimeListAnimeListClient;

    public AnimeList getAnimeListUseCase(String token, String status) {
        return myAnimeListAnimeListClient.getAnimeList(token, status);
    }

    public AnimeStatus getAnimeListStatus(String token, int id) {
        return myAnimeListAnimeListClient.getAnimeStatus(token, id);
    }

    public AnimeStatus addAnimeToList(String token, int id) {
        return myAnimeListAnimeListClient.addAnimeToList(token, id);
    }

    public AnimeStatus modifyAnimeToList(String token, int id, String status, int score, int numEpisodes) {
        return myAnimeListAnimeListClient.modifyAnimeToList(token, id, status, score, numEpisodes);
    }
}
