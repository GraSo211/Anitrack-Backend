package com.graso.anitrack.anime.infrastructure.mapper;

import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.infrastructure.anilist.dto.ResponseAniListDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class AniListAnimeMapper {

    public Anime toDomain(ResponseAniListDto response) {

        ResponseAniListDto.Data.Media media = response.data().media();

        Optional<LocalDate> startDate =
                media.startDate()
                        .map(sd -> LocalDate.of(sd.year(), sd.month(), sd.day()));

        Optional<String> image =
                media.coverImage()
                        .map(ResponseAniListDto.Data.CoverImage::extraLarge);

        return new Anime(
                media.id(),
                media.idMal(),

                media.title().romaji(),
                media.description(),
                media.status(),

                media.bannerImage(),
                image,

                media.genres(),

                media.averageScore(),
                media.popularity(),

                media.source(),

                media.episodes(),
                startDate,
                media.duration(),

                media.isAdult()
        );
    }
}
