package com.graso.anitrack.external.myanimelist.mapper;

import com.graso.anitrack.animelist.domain.AnimeStatus;
import com.graso.anitrack.external.myanimelist.dto.ResponseAnimeStatusRequest;
import org.springframework.stereotype.Component;

@Component
public class MyAnimeListStatusMapper {

    public AnimeStatus toAnimeStatus(ResponseAnimeStatusRequest response) {

        if (response == null || response.my_list_status() == null) {
            return new AnimeStatus(
                    "none",
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
}