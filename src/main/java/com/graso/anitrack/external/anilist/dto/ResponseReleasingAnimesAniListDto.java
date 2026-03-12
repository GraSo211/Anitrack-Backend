package com.graso.anitrack.external.anilist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graso.anitrack.anime.domain.anime.AiringSchedule;
import com.graso.anitrack.anime.domain.anime.valueobject.MediaCoverImage;
import com.graso.anitrack.anime.domain.anime.valueobject.MediaTitle;

import java.util.List;

public record ResponseReleasingAnimesAniListDto(Data data) {
    public record Data(@JsonProperty("Page") Page page) {
        public record Page(
                PageInfo pageInfo,
                List<Media> media
        ) {
            public record PageInfo(
                    int currentPage,
                    int lastPage
            ) {
            }

            public record Media(
                    int id,
                    Integer idMal,
                    MediaTitle title,
                    MediaCoverImage coverImage,
                    AiringSchedule nextAiringEpisode
            ) {

            }
        }
    }
}
