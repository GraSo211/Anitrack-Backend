package com.graso.anitrack.external.anilist;


import com.graso.anitrack.anime.application.dto.AnimeCard;
import com.graso.anitrack.anime.application.dto.AnimeReleasing;
import com.graso.anitrack.anime.application.dto.AnimeTopSeason;
import com.graso.anitrack.anime.domain.anime.Anime;
import com.graso.anitrack.anime.domain.anime.valueobject.MediaSeason;
import com.graso.anitrack.anime.domain.genre.Genre;
import com.graso.anitrack.anime.domain.genre.Tag;
import com.graso.anitrack.external.anilist.dto.*;
import com.graso.anitrack.external.anilist.mapper.AniListAnimeMapper;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;

@Component
@AllArgsConstructor
public class AniListApiClient {
    private WebClient aniListWebClient;
    private AniListAnimeMapper aniListAnimeMapper;


    private <T> T executeQuery(String query, Map<String, Object> variables, Class<T> responseType) {

        PostQueryDto body = new PostQueryDto(query, variables);

        return aniListWebClient
                .post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

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
        ResponseFetchByIdAniListDto response = executeQuery(query, Map.of("id", id), ResponseFetchByIdAniListDto.class);

        return aniListAnimeMapper.toDomain(response);
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
        ResponseBannerImageAniListDto response = executeQuery(query, null, ResponseBannerImageAniListDto.class);
        if (response == null || response.data() == null || response.data().page() == null) {
            return null;
        }

        String imageLink = response
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
    public AnimeTopSeason fetchTopAnimeSeason() {
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
        ResponseTopSeasonAnimeDto response = executeQuery(query, null, ResponseTopSeasonAnimeDto.class);


        if (response == null || response.data() == null) {
            return null;
        }
        ResponseTopSeasonAnimeDto.Data.AnimeData topScored = response.data().topScored();
        ResponseTopSeasonAnimeDto.Data.AnimeData topPopular = response.data().topPopular();
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
            ResponseReleasingAnimesAniListDto response = executeQuery(query, Map.of("page", nPage, "perPage", 50), ResponseReleasingAnimesAniListDto.class);


            if (response == null || response.data() == null) {
                return Collections.emptyList();
            }
            lastPage = response.data().page().pageInfo().lastPage();
            nPage++;
            releasingAnimes.addAll(response.data()
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

        ResponseAnimeCardAniListDto response = executeQuery(query, null, ResponseAnimeCardAniListDto.class);


        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        List<AnimeCard> trendingAnimes = response.data()
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


        ResponseAnimeCardAniListDto response = executeQuery(query, null, ResponseAnimeCardAniListDto.class);


        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        List<AnimeCard> trendingAnimes = response.data()
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

        ResponseAnimeCardAniListDto response = executeQuery(query, null, ResponseAnimeCardAniListDto.class);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        List<AnimeCard> trendingAnimes = response.data()
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

        ResponseTagsAniListDto response = executeQuery(query, null, ResponseTagsAniListDto.class);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        List<Tag> tags = response.data()
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

        ResponseGenresAniListDto response = executeQuery(query, null, ResponseGenresAniListDto.class);


        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        List<Genre> genres = response.data()
                .genreCollection()
                .stream()
                .map(genre -> aniListAnimeMapper.toGenre(genre))
                .toList();
        return genres;
    }

    public List<AnimeCard> fetchAnimesByFilters(int cant, String name, List<String> tags, List<String> genres, int year, String season, String status) {
        String query = String.format("""
                             query ($genre_in: [String], $tag_in: [String], $season: MediaSeason, $seasonYear: Int, $status: MediaStatus, $search: String) {
                               Page(perPage: %d) {
                                 media(type: ANIME, genre_in: $genre_in, tag_in: $tag_in, season: $season, seasonYear: $seasonYear, status: $status, search: $search, sort: POPULARITY_DESC) {
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
                """, cant);

        Map<String, Object> variables = new HashMap<>();

        if (name != null) {
            variables.put("search", name);
        }

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


        ResponseAnimeCardAniListDto response = executeQuery(query, variables, ResponseAnimeCardAniListDto.class);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        List<AnimeCard> filteredAnimes = response.data()
                .page()
                .media()
                .stream()
                .map(anime -> aniListAnimeMapper.toAnimeCard(anime))
                .toList();
        return filteredAnimes;
    }


}

