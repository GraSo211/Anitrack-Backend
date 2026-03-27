package com.graso.anitrack.external.myanimelist.mapper;

import com.graso.anitrack.animelist.domain.AnimeStatus;
import com.graso.anitrack.external.myanimelist.dto.ResponseAnimeStatusRequest;
import com.graso.anitrack.external.myanimelist.dto.ResponseAnimeToListRequest;
import org.springframework.stereotype.Component;

@Component
public class MyAnimeListStatusMapper {

    public AnimeStatus toAnimeStatus(ResponseAnimeStatusRequest response) {

        if (response == null || response.my_list_status() == null) {
            return new AnimeStatus(
                    null,
                    0,
                    0,
                    false
            );
        }

        ResponseAnimeStatusRequest.MyListStatus status = response.my_list_status();

        return new AnimeStatus(
                status.status(),
                status.score(),
                status.num_episodes_watched(),
                status.is_rewatching()
        );
    }

    public AnimeStatus fromAnimeToListToAnimeStatus(ResponseAnimeToListRequest response) {

        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }

        if (response.status() == null) {
            throw new IllegalStateException("Status is required but was null");
        }

        return new AnimeStatus(
                response.status(),
                response.score() != null ? response.score() : 0,
                response.numEpisodesWatched() != null ? response.numEpisodesWatched() : 0,
                response.isRewatching() != null ? response.isRewatching() : false
        );
    }
}