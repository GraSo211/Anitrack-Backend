package com.graso.anitrack.anime.infrastructure.anilist.client;


import com.graso.anitrack.anime.domain.model.*;
import com.graso.anitrack.anime.domain.model.genres.Genre;
import com.graso.anitrack.anime.domain.model.genres.Tag;
import com.graso.anitrack.anime.infrastructure.anilist.dto.*;
import com.graso.anitrack.anime.infrastructure.mapper.AniListAnimeMapper;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;

@Component
@AllArgsConstructor
public class AniListApiClient {
    private WebClient.Builder webClientBuilder;
    private AniListAnimeMapper aniListAnimeMapper;

    @Cacheable(value = "animeByIdCache", key = "#id", unless = "#result == null")
    public Anime fetchAnimeById(Long id) {
        final String query =
                """
                         query($id:Int) {
                           Media(id: $id) {
                             id
                             idMal
                             title {
                               romaji
                               english
                             }
                             status
                             description(asHtml: true)
                             startDate{
                                year
                                month
                                day
                             }
                             endDate{
                                year
                                month
                                day
                             }
                             season
                             seasonYear
                             episodes
                             duration
                             countryOfOrigin
                             source
                             trailer{
                                 id
                                 site
                                 thumbnail
                             }
                             coverImage{
                                extraLarge
                                large
                                medium
                                color
                             }
                             bannerImage
                             genres
                             synonyms
                             averageScore
                             popularity
                             relations {
                               edges {
                                 relationType
                                 node {
                                   id
                                   type
                                   title{
                                        romaji
                                   }
                                   coverImage{
                                        extraLarge
                                        large
                                        medium
                                        color
                                   }
                                 }
                               }
                             }
                             studios{
                                edges{
                                    isMain
                                    node{
                                        id
                                        name
                                    }
                                }
                             }
                             isAdult
                             nextAiringEpisode{
                                id
                                airingAt
                                timeUntilAiring
                                episode
                                mediaId
                             }
                           }
                         }   
                        """;
        PostQueryDto postQueryDto = new PostQueryDto(query, Map.of("id", id.longValue()));
        Mono<ResponseFetchByIdAniListDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseFetchByIdAniListDto.class);
        ResponseFetchByIdAniListDto responseAniListDto = responseDtoMono.block();
        return aniListAnimeMapper.toDomain(responseAniListDto);
    }

    @Cacheable(value = "bannerImageCache", unless = "#result == null")
    public Map<String, String> fetchBannerImage() {
        final String currentSeason = MediaSeason.current().toString();
        final int currentYear = LocalDate.now().getYear();
        final String query = String.format("""
                query{
                    Page(perPage: 10) {
                        media(
                        type: ANIME
                        season: %s
                        seasonYear: %d
                        sort: POPULARITY_DESC
                        ) {
                        bannerImage
                        }
                    }
                }
                """, currentSeason, currentYear);
        PostQueryDto postQueryDto = new PostQueryDto(query, null);
        Mono<ResponseBannerImageAniListDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseBannerImageAniListDto.class);
        ResponseBannerImageAniListDto responseBannerImageAniListDto = responseDtoMono.block();
        if (responseBannerImageAniListDto == null || responseBannerImageAniListDto.data() == null || responseBannerImageAniListDto.data().page() == null) {
            return null;
        }

        String imageLink = responseBannerImageAniListDto
                .data()
                .page()
                .media()
                .stream()
                .map(ResponseBannerImageAniListDto.Data.Page.Media::bannerImage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);


        return Map.of("link", imageLink);
    }

    @Cacheable(value = "topSeasonCache", unless = "#result == null")
    public AnimeTopSeason findTopAnimeSeason() {
        String actualSeason = MediaSeason.current().toString();
        int actualYear = LocalDate.now().getYear();
        final String query = String.format("""
                   query{
                        topScored: Page(perPage: 5) {
                            media(
                            season: %s
                            seasonYear: %d
                            type: ANIME
                            sort: SCORE_DESC
                            ) {
                            id
                            title {
                                romaji
                                english
                            }
                            bannerImage
                            meanScore
                            popularity
                            }
                        }
                
                        topPopular: Page(perPage: 5) {
                            media(
                            season: %s
                            seasonYear: %d
                            type: ANIME
                            sort: POPULARITY_DESC
                            ) {
                            id
                            title {
                                romaji
                                english
                            }
                            bannerImage
                            meanScore
                            popularity
                            }
                        }
                
                    }
                """, actualSeason, actualYear, actualSeason, actualYear
        );
        PostQueryDto postQueryDto = new PostQueryDto(query, null);
        Mono<ResponseTopSeasonAnimeDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseTopSeasonAnimeDto.class);
        ResponseTopSeasonAnimeDto responseTopSeasonAnimeDto = responseDtoMono.block();

        if (responseTopSeasonAnimeDto == null || responseTopSeasonAnimeDto.data() == null) {
            return null;
        }
        ResponseTopSeasonAnimeDto.Data.AnimeData topScored = responseTopSeasonAnimeDto.data().topScored();
        ResponseTopSeasonAnimeDto.Data.AnimeData topPopular = responseTopSeasonAnimeDto.data().topPopular();
        List<ResponseTopSeasonAnimeDto.Data.AnimeData.Media> listAnime = new ArrayList<>();
        listAnime.addAll(topScored.media());
        listAnime.addAll(topPopular.media());
        listAnime = listAnime.stream().filter(i -> i.bannerImage() != null).toList();
        listAnime = listAnime.stream().sorted(
                Comparator
                        .comparing(ResponseTopSeasonAnimeDto.Data.AnimeData.Media::meanScore,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ResponseTopSeasonAnimeDto.Data.AnimeData.Media::popularity,
                                Comparator.nullsLast(Comparator.reverseOrder()))
        ).toList();

        ResponseTopSeasonAnimeDto.Data.AnimeData.Media topSeasonAnime = listAnime.stream().findFirst().orElse(null);
        if (topSeasonAnime == null) {
            return null;
        }
        return aniListAnimeMapper.toAnimeTopSeason(topSeasonAnime);
    }

    public List<AnimeName> fetchAnimeByName(String name) {
        final String query = String.format("""
                      query($search: String){
                             Page(perPage: 5) {
                                   media(search: $search, type: ANIME) {
                                         id
                                         idMal
                                         title {
                                               romaji
                                               english
                                         }
                                         coverImage {
                                               extraLarge
                                               large
                                               medium
                                               color
                                         }
                                   }
                             }
                      }
                """, name);
        PostQueryDto postQueryDto = new PostQueryDto(query, Map.of("search", name));
        Mono<ResponseFetchByNameAniListDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseFetchByNameAniListDto.class);
        ResponseFetchByNameAniListDto responseAniListDto = responseDtoMono.block();
        if (responseAniListDto == null || responseAniListDto.data() == null) {
            return null;
        }
        List<AnimeName> animesByName = new ArrayList<>();
        animesByName = responseAniListDto.data()
                .page()
                .media()
                .stream()
                .map(anime -> aniListAnimeMapper.toAnimeName(anime))
                .toList();
        return animesByName;
    }

    @Cacheable(value = "releasingAnimesCache", unless = "#result == null")
    public List<AnimeReleasing> fetchReleasingAnimes() {
        final String query = """
                        query ($page: Int, $perPage: Int) {
                          Page(page: $page, perPage: $perPage) {
                            pageInfo {
                              currentPage
                              lastPage
                            }
                            media(type: ANIME, status: RELEASING, isAdult: false) {
                              id
                              idMal
                              title {
                                romaji
                                english
                              }
                              coverImage {
                                extraLarge
                                large
                                medium
                                color
                              }
                              nextAiringEpisode {
                                id
                                airingAt
                                timeUntilAiring
                                episode
                                mediaId
                              }
                            }
                          }
                        }
                """;
        int nPage = 1;
        int lastPage = 1;
        List<AnimeReleasing> releasingAnimes = new ArrayList<>();
        do {
            PostQueryDto postQueryDto = new PostQueryDto(query, Map.of("page", nPage, "perPage", 50));
            Mono<ResponseReleasingAnimesAniListDto> responseDtoMono = webClientBuilder.build()
                    .post()
                    .uri("https://graphql.anilist.co")
                    .bodyValue(postQueryDto)
                    .retrieve()
                    .bodyToMono(ResponseReleasingAnimesAniListDto.class);
            ResponseReleasingAnimesAniListDto responseAniListDto = responseDtoMono.block();
            if (responseAniListDto == null || responseAniListDto.data() == null) {
                return Collections.emptyList();
            }
            lastPage = responseAniListDto.data().page().pageInfo().lastPage();
            nPage++;
            releasingAnimes.addAll(responseAniListDto.data()
                    .page()
                    .media()
                    .stream()
                    .map(anime -> aniListAnimeMapper.toAnimeReleasing(anime))
                    .toList());
        }
        while (nPage <= lastPage);

        return releasingAnimes;
    }

    @Cacheable(value = "upcomingReleasesCache", unless = "#result == null")
    public List<AnimeCard> fetchUpcomingAnimeReleases() {
        final String query = """
                        query {
                          Page(perPage: 5) {
                            media(type: ANIME, status: NOT_YET_RELEASED, sort: [POPULARITY_DESC,TRENDING_DESC]) {
                              id
                              idMal
                              title {
                                romaji
                                english
                              }
                              coverImage {
                                extraLarge
                                large
                                medium
                                color
                              }
                            }
                          }
                        }
                
                """;
        PostQueryDto postQueryDto = new PostQueryDto(query, null);
        Mono<ResponseAnimeCardAniListDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseAnimeCardAniListDto.class);
        ResponseAnimeCardAniListDto responseAniListDto = responseDtoMono.block();
        if (responseAniListDto == null || responseAniListDto.data() == null) {
            return Collections.emptyList();
        }
        List<AnimeCard> trendingAnimes = responseAniListDto.data()
                .page()
                .media()
                .stream()
                .map(anime -> aniListAnimeMapper.toAnimeCard(anime))
                .toList();
        return trendingAnimes;
    }

    @Cacheable(value = "seasonTrendCache", unless = "#result == null")
    public List<AnimeCard> fetchSeasonTrendAnimes() {
        MediaSeason actualSeason = MediaSeason.current();
        int actualYear = LocalDate.now().getYear();
        final String query = String.format("""
                        query {
                          Page(perPage: 5) {
                            media(type: ANIME, sort: POPULARITY_DESC, season: %s, seasonYear: %d) {
                              id
                              idMal
                              title {
                                romaji
                                english
                              }
                              coverImage {
                                extraLarge
                                large
                                medium
                                color
                              }
                            }
                          }
                        }
                
                """, actualSeason.toString(), actualYear);
        PostQueryDto postQueryDto = new PostQueryDto(query, null);
        Mono<ResponseAnimeCardAniListDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseAnimeCardAniListDto.class);
        ResponseAnimeCardAniListDto responseAniListDto = responseDtoMono.block();
        if (responseAniListDto == null || responseAniListDto.data() == null) {
            return Collections.emptyList();
        }
        List<AnimeCard> trendingAnimes = responseAniListDto.data()
                .page()
                .media()
                .stream()
                .map(anime -> aniListAnimeMapper.toAnimeCard(anime))
                .toList();
        return trendingAnimes;
    }

    @Cacheable(value = "mostValoratedCache", unless = "#result == null")
    public List<AnimeCard> fetchMostValoratedAnimes() {
        final String query = """
                        query {
                          Page(perPage: 5) {
                            media(type: ANIME, sort: SCORE_DESC) {
                              id
                              idMal
                              title {
                                romaji
                                english
                              }
                              coverImage {
                                extraLarge
                                large
                                medium
                                color
                              }
                            }
                          }
                        }
                
                """;
        PostQueryDto postQueryDto = new PostQueryDto(query, null);
        Mono<ResponseAnimeCardAniListDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseAnimeCardAniListDto.class);

        ResponseAnimeCardAniListDto responseAniListDto = responseDtoMono.block();
        if (responseAniListDto == null || responseAniListDto.data() == null) {
            return Collections.emptyList();
        }
        List<AnimeCard> trendingAnimes = responseAniListDto.data()
                .page()
                .media()
                .stream()
                .map(anime -> aniListAnimeMapper.toAnimeCard(anime))
                .toList();
        return trendingAnimes;
    }

    @Cacheable(value = "genreAnimeCache", key = "#genre", unless = "#result == null")
    public List<AnimeCard> fetchAnimesByGenre(String genre) {
        final String query = """
                        query ($genre: String) {
                          Page(perPage: 5) {
                            media(type: ANIME, genre_in: [$genre], sort: POPULARITY_DESC) {
                              id
                              idMal
                              title {
                                romaji
                                english
                              }
                              coverImage {
                                extraLarge
                                large
                                medium
                                color
                              }
                            }
                          }
                        }
                
                """;
        PostQueryDto postQueryDto = new PostQueryDto(query, Map.of("genre", genre));
        Mono<ResponseAnimeCardAniListDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseAnimeCardAniListDto.class);
        ResponseAnimeCardAniListDto responseAniListDto = responseDtoMono.block();
        if (responseAniListDto == null || responseAniListDto.data() == null) {
            return Collections.emptyList();
        }
        List<AnimeCard> trendingAnimes = responseAniListDto.data()
                .page()
                .media()
                .stream()
                .map(anime -> aniListAnimeMapper.toAnimeCard(anime))
                .toList();
        return trendingAnimes;
    }

    public List<Tag> fetchAllTags() {
        final String query = """
                        query {
                          MediaTagCollection {
                            name
                            description
                            isAdult
                          }
                        }
                """;
        PostQueryDto postQueryDto = new PostQueryDto(query, null);
        Mono<ResponseTagsAniListDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseTagsAniListDto.class);
        ResponseTagsAniListDto responseTagsAniListDto = responseDtoMono.block();
        if (responseTagsAniListDto == null || responseTagsAniListDto.data() == null) {
            return Collections.emptyList();
        }
        List<Tag> tags = responseTagsAniListDto.data()
                .mediaTagCollection()
                .stream()
                .map(tag -> aniListAnimeMapper.toTag(tag))
                .toList();
        return tags;
    }

    public List<Genre> fetchAllGenres() {
        final String query = """
                        query {
                          GenreCollection
                        }
                """;
        PostQueryDto postQueryDto = new PostQueryDto(query, null);
        Mono<ResponseGenresAniListDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseGenresAniListDto.class);
        ResponseGenresAniListDto responseGenresAniListDto = responseDtoMono.block();
        if (responseGenresAniListDto == null || responseGenresAniListDto.data() == null) {
            return Collections.emptyList();
        }
        List<Genre> genres = responseGenresAniListDto.data()
                .genreCollection()
                .stream()
                .map(genre -> aniListAnimeMapper.toGenre(genre))
                .toList();
        return genres;
    }

    public List<AnimeCard> fetchAnimesByFilters(List<String> tags, List<String> genres, int year, String season, String status) {
        String query = """
                        query ($genre_in: [String], $tag_in: [String], $season: MediaSeason, $seasonYear: Int, $status: MediaStatus) {
                          Page(perPage: 25) {
                            media(type: ANIME, genre_in: $genre_in, tag_in: $tag_in, season: $season, seasonYear: $seasonYear, status: $status, sort: POPULARITY_DESC) {
                              id
                              idMal
                              title {
                                romaji
                                english
                              }
                              coverImage {
                                extraLarge
                                large
                                medium
                                color
                              }
                            }
                          }
                        }
                """;

        Map<String, Object> variables = new HashMap<>();

        if (genres != null && !genres.isEmpty()) {
            variables.put("genre_in", genres);
        }

        if (tags != null && !tags.isEmpty()) {
            variables.put("tag_in", tags);
        }

        if (season != null) {
            variables.put("season", season);
        }

        if (year > 0) {
            variables.put("seasonYear", year);
        }

        if (status != null) {
            variables.put("status", status);
        }

        PostQueryDto postQueryDto = new PostQueryDto(query, variables);
        Mono<ResponseAnimeCardAniListDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseAnimeCardAniListDto.class);
        ResponseAnimeCardAniListDto responseAniListDto = responseDtoMono.block();
        if (responseAniListDto == null || responseAniListDto.data() == null) {
            return Collections.emptyList();
        }
        List<AnimeCard> filteredAnimes = responseAniListDto.data()
                .page()
                .media()
                .stream()
                .map(anime -> aniListAnimeMapper.toAnimeCard(anime))
                .toList();
        return filteredAnimes;
    }


}

