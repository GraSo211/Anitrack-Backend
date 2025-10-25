package com.graso.anitrack.anime.infrastructure.anilist.dto;

import java.util.Date;
import java.util.List;

public record ResponseAniListDto(Data data) {
    public record Data(Media media){
        public record  Media(Long id, Long idMal, Title title, CoverImage coverImage, String description,
                             String bannerImage, int episodes, StartDate startDate, int duration, boolean isAdult,
                             List<String> genres, int averageScore, int popularity, String source, String status,
                             NextAiringEpisode nextAiringEpisode){



        }
        public record Title(String romaji){};
        public record CoverImage(String extraLarge, String large){};
        public record StartDate(int year, int month, int day){};
        public record NextAiringEpisode(Date airingAt, int id, int episode){};
    }
}
