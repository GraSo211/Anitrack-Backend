package com.graso.anitrack.external.anilist.mapper;

import com.graso.anitrack.anime.application.dto.AnimeCard;
import com.graso.anitrack.anime.application.dto.AnimeReleasing;
import com.graso.anitrack.anime.application.dto.AnimeTopSeason;
import com.graso.anitrack.anime.domain.anime.AiringSchedule;
import com.graso.anitrack.anime.domain.anime.Anime;
import com.graso.anitrack.anime.domain.anime.valueobject.*;
import com.graso.anitrack.anime.domain.genre.Genre;
import com.graso.anitrack.anime.domain.genre.Tag;
import com.graso.anitrack.external.anilist.dto.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AniListAnimeMapper {

    public Anime toDomain(ResponseFetchByIdAniListDto response) {

        ResponseFetchByIdAniListDto.Data.Media dto = response.data().media();
        String cleanDescription = Jsoup.clean(
                dto.description(),
                Safelist.basic()
        );
        return new Anime(
                dto.id(),
                dto.idMal() != null ? dto.idMal().intValue() : null,

                mapTitle(dto.title()),
                mapStatus(dto.status()),
                cleanDescription,
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

                dto.isAdult() != null ? dto.isAdult() : false,

                mapAiringSchedule(dto.nextAiringEpisode())
        );
    }

    public AnimeTopSeason toAnimeTopSeason(ResponseTopSeasonAnimeDto.Data.AnimeData.Media response) {

        return new AnimeTopSeason(
                response.id(),
                mapTitle(response.title()),
                response.bannerImage(),
                response.meanScore(),
                response.popularity()
        );

    }


    public AnimeReleasing toAnimeReleasing(ResponseReleasingAnimesAniListDto.Data.Page.Media response) {
        return new AnimeReleasing(
                response.id(),
                response.idMal(),
                response.title(),
                response.coverImage(),
                response.nextAiringEpisode()
        );
    }


    public AnimeCard toAnimeCard(ResponseAnimeCardAniListDto.Data.Page.Media response) {
        return new AnimeCard(
                response.id(),
                response.idMal(),
                response.title(),
                response.coverImage()
        );
    }


    public Tag toTag(ResponseTagsAniListDto.Data.MediaTagCollection response) {
        return new Tag(
                response.name(),
                response.description(),
                response.isAdult()
        );
    }

    public Genre toGenre(String response) {
        return new Genre(
                response
        );
    }

    // =========================
    // MAPPERS
    // =========================

    private MediaTitle mapTitle(ResponseTopSeasonAnimeDto.Data.AnimeData.Media.Title title) {
        if (title == null) return null;
        return new MediaTitle(title.romaji(), title.english());
    }

    private MediaTitle mapTitle(ResponseFetchByIdAniListDto.Data.Title title) {
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

    private FuzzyDate mapFuzzyDate(ResponseFetchByIdAniListDto.Data.StartDate date) {
        if (date == null) return null;
        if (date.year() == null || date.month() == null || date.day() == null) {
            return null;
        }
        return new FuzzyDate(date.year(), date.month(), date.day());
    }

    private FuzzyDate mapFuzzyDate(ResponseFetchByIdAniListDto.Data.EndDate date) {
        if (date == null) return null;
        if (date.year() == null || date.month() == null || date.day() == null) {
            return null;
        }
        return new FuzzyDate(date.year(), date.month(), date.day());
    }

    private MediaTrailer mapTrailer(ResponseFetchByIdAniListDto.Data.Trailer trailer) {
        if (trailer == null) return null;
        return new MediaTrailer(
                trailer.id(),
                trailer.site(),
                trailer.thumbnail()
        );
    }

    private MediaCoverImage mapCoverImage(ResponseFetchByIdAniListDto.Data.CoverImage cover) {
        if (cover == null) return null;
        return new MediaCoverImage(
                cover.extraLarge(),
                cover.large(),
                cover.medium(),
                cover.color()
        );
    }

    private AiringSchedule mapAiringSchedule(ResponseFetchByIdAniListDto.Data.NextAiringEpisode airing) {
        if (airing == null) return null;
        return new AiringSchedule(
                airing.id(),
                airing.airingAt(),
                airing.timeUntilAiring(),
                airing.episode(),
                airing.mediaId()
        );
    }

    private MediaRelations mapRelations(ResponseFetchByIdAniListDto.Data.Relations relations) {
        if (relations == null || relations.edges() == null) {
            return MediaRelations.empty();
        }

        return new MediaRelations(
                relations.edges().stream()
                        .map(edge -> new MediaRelation(
                                edge.node().id(),
                                TypeMediaRelation.valueOf(edge.relationType()),
                                edge.node().type(),
                                edge.node().title().romaji(),
                                edge.node().coverImage().extraLarge()
                        ))
                        .toList()
        );
    }

    private String extractMainStudio(ResponseFetchByIdAniListDto.Data.Studios studios) {
        if (studios == null || studios.edges() == null) return null;

        return studios.edges().stream()
                .filter(ResponseFetchByIdAniListDto.Data.StudioEdge::isMain)
                .map(edge -> edge.node().name())
                .findFirst()
                .orElse(null);
    }

    private <T> List<T> safeList(List<T> list) {
        return list != null ? list : List.of();
    }
}
