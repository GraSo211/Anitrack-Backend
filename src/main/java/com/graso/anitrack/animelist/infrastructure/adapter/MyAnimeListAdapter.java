package com.graso.anitrack.animelist.infrastructure.adapter;

import com.graso.anitrack.animelist.application.out.AnimeListCommandPort;
import com.graso.anitrack.animelist.application.out.AnimeListQueryPort;
import com.graso.anitrack.animelist.domain.AnimeList;
import com.graso.anitrack.animelist.domain.AnimeStatus;
import com.graso.anitrack.external.myanimelist.MyAnimeListApiClient;
import com.graso.anitrack.external.myanimelist.dto.ResponseAnimeListRequest;
import com.graso.anitrack.external.myanimelist.dto.ResponseAnimeStatusRequest;
import com.graso.anitrack.external.myanimelist.dto.ResponseAnimeToListRequest;
import com.graso.anitrack.external.myanimelist.mapper.MyAnimeListAnimeListMapper;
import com.graso.anitrack.external.myanimelist.mapper.MyAnimeListStatusMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MyAnimeListAdapter implements AnimeListQueryPort, AnimeListCommandPort {
    private final MyAnimeListApiClient myAnimeListApiClient;
    private final MyAnimeListAnimeListMapper myAnimeListAnimeListMapper;
    private final MyAnimeListStatusMapper myAnimeListStatusMapper;

    @Override
    public AnimeList findAnimeList(String token, String status) {
        ResponseAnimeListRequest response = myAnimeListApiClient.getAnimeList(token, status);
        return myAnimeListAnimeListMapper.toAnimeList(response);
    }

    @Override
    public AnimeStatus findAnimeListStatus(String token, int id) {
        ResponseAnimeStatusRequest response = myAnimeListApiClient.getAnimeStatus(token, id);
        return myAnimeListStatusMapper.toAnimeStatus(response);
    }

    @Override
    public AnimeStatus addAnimeToList(String token, int id) {
        ResponseAnimeToListRequest response = myAnimeListApiClient.addAnimeToList(token, id);
        return myAnimeListStatusMapper.fromAnimeToListToAnimeStatus(response);
    }

    @Override
    public AnimeStatus modifyAnimeToList(String token, int id, String status, int score, int numEpisodes) {
        ResponseAnimeToListRequest response = myAnimeListApiClient.modifyAnimeToList(token, id, status, score, numEpisodes);
        return myAnimeListStatusMapper.fromAnimeToListToAnimeStatus(response);
    }
}
