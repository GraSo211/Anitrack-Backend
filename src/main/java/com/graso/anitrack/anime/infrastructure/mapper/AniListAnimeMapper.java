package com.graso.anitrack.anime.infrastructure.mapper;

import com.graso.anitrack.anime.domain.model.*;
import com.graso.anitrack.anime.infrastructure.anilist.dto.ResponseAniListDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class AniListAnimeMapper {

    public Media toDomain(ResponseAniListDto response) {

        ResponseAniListDto.Data.Media dto = response.data().media();

        return new Media(
                dto.id().intValue(),
                dto.idMal() != null ? dto.idMal().intValue() : null,

                mapTitle(dto.title()),
                mapStatus(dto.status()),
                dto.description(),

                mapFuzzyDate(dto.startDate()),
                mapFuzzyDate(dto.endDate()),

                mapSeason(dto.season()),
                dto.seasonYear(),

                dto.episodes(),
                dto.duration(),

                null, // countryOfOrigin (no lo estás trayendo todavía)

                mapSource(dto.source()),

                mapTrailer(dto.trailer()),
                mapCoverImage(dto.coverImage()),
                dto.bannerImage(),

                safeList(dto.genres()),
                safeList(dto.synonyms()),

                dto.averageScore(),
                dto.popularity(),

                mapRelations(dto.relations()),

                extractMainStudio(dto.studios()),

                dto.isAdult() != null? dto.isAdult() : false,

                mapAiringSchedule(dto.nextAiringEpisode())
        );
    }

    // =========================
    // MAPPERS
    // =========================

    private MediaTitle mapTitle(ResponseAniListDto.Data.Title title) {
        if (title == null) return null;
        return new MediaTitle(title.romaji(), title.english());
    }

    private MediaStatus mapStatus(String status) {
        return status != null ? MediaStatus.valueOf(status) : null;
    }

    private MediaSeason mapSeason(String season) {
        return season != null ? MediaSeason.valueOf(season) : null;
    }

    private MediaSource mapSource(String source) {
        return source != null ? MediaSource.valueOf(source) : null;
    }

    private FuzzyDate mapFuzzyDate(ResponseAniListDto.Data.StartDate date) {
        if (date == null) return null;
        if (date.year() == null || date.month() == null || date.day() == null) {
            return null;
        }
        return new FuzzyDate(date.year(), date.month(), date.day());
    }

    private FuzzyDate mapFuzzyDate(ResponseAniListDto.Data.EndDate date) {
        if (date == null) return null;
        if (date.year() == null || date.month() == null || date.day() == null) {
            return null;
        }
        return new FuzzyDate(date.year(), date.month(), date.day());
    }

    private MediaTrailer mapTrailer(ResponseAniListDto.Data.Trailer trailer) {
        if (trailer == null) return null;
        return new MediaTrailer(
                trailer.id(),
                trailer.site(),
                trailer.thumbnail()
        );
    }

    private MediaCoverImage mapCoverImage(ResponseAniListDto.Data.CoverImage cover) {
        if (cover == null) return null;
        return new MediaCoverImage(
                cover.extraLarge(),
                cover.large(),
                cover.medium(),
                cover.color()
        );
    }

    private AiringSchedule mapAiringSchedule(ResponseAniListDto.Data.NextAiringEpisode airing) {
        if (airing == null) return null;
        return new AiringSchedule(
                airing.id(),
                airing.airingAt(),
                airing.timeUntilAiring(),
                airing.episode(),
                airing.mediaId()
        );
    }

    private MediaRelations mapRelations(ResponseAniListDto.Data.Relations relations) {
        if (relations == null || relations.edges() == null) {
            return MediaRelations.empty();
        }

        return new MediaRelations(
                relations.edges().stream()
                        .map(edge -> new MediaRelation(
                                edge.node().id().intValue(),
                                TypeMediaRelation.valueOf(edge.relationType()),
                                edge.node().title().romaji(),
                                edge.node().coverImage().extraLarge()
                        ))
                        .toList()
        );
    }

    private String extractMainStudio(ResponseAniListDto.Data.Studios studios) {
        if (studios == null || studios.edges() == null) return null;

        return studios.edges().stream()
                .filter(ResponseAniListDto.Data.StudioEdge::isMain)
                .map(edge -> edge.node().name())
                .findFirst()
                .orElse(null);
    }

    private <T> List<T> safeList(List<T> list) {
        return list != null ? list : List.of();
    }
}
