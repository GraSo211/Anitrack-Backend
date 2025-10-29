package com.graso.anitrack.anime.infrastructure.anilist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public record ResponseAniListDto(Data data) {
    public record Data(@JsonProperty("Media") Media media){
        public record  Media(Long id, Long idMal, Title title, Optional<CoverImage> coverImage, String description,
                             Optional<String> bannerImage, Optional<Integer> episodes, Optional<StartDate> startDate, Optional<Integer> duration, boolean isAdult,
                             List<String> genres, Optional<Integer> averageScore, Optional<Integer> popularity, Optional<String> source, String status,
                             Optional<NextAiringEpisode> nextAiringEpisode){



        }
        public record Title(String romaji){};
        public record CoverImage(String extraLarge, String large){};
        public record StartDate(int year, int month, int day){};
        public record NextAiringEpisode(Date airingAt, int id, int episode){};
    }
}
