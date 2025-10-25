package com.graso.anitrack.anime.infrastructure.mapper;

import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.infrastructure.anilist.dto.ResponseAniListDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AniListAnimeMapper {

    public Anime toDomain(ResponseAniListDto response) {

        ResponseAniListDto.Data.Media media = response.data().media();

        LocalDate startDate = media.startDate() != null
                ? LocalDate.of(
                media.startDate().year(),
                media.startDate().month(),
                media.startDate().day()
        )
                : null;

        return new Anime(
                media.id(),
                media.idMal(),
                media.title().romaji(),
                media.description(),
                media.status(),
                media.bannerImage(),
                media.averageScore(),
                media.popularity(),
                media.genres(),
                media.coverImage() != null ? media.coverImage().extraLarge() : null,
                media.source(),
                media.episodes(),
                startDate,
                media.duration()
        );
    }
}
