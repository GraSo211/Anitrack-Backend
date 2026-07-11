package com.graso.anitrack.external.anilist;

import com.graso.anitrack.anime.domain.anime.valueobject.MediaSeason;
import com.graso.anitrack.external.anilist.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Component
@AllArgsConstructor
public class AniListApiClient {
    private WebClient aniListWebClient;

    private <T> T executeQuery(String query, Map<String, Object> variables, Class<T> responseType) {
        PostQueryDto body = new PostQueryDto(query, variables);
        return aniListWebClient
                .post()
                .uri("")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block(Duration.ofSeconds(10));
    }

    private static final String FETCH_ANIME_BY_ID_QUERY = """
            query($id: Int, $idMal: Int) {
              Media(id: $id, idMal: $idMal, type: ANIME) {
                id
                idMal
                title {
                  romaji
                  english
                }
                status
                description(asHtml: true)
                startDate {
                  year
                  month
                  day
                }
                endDate {
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
                trailer {
                  id
                  site
                  thumbnail
                }
                coverImage {
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
                      title {
                        romaji
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
                studios {
                  edges {
                    isMain
                    node {
                      id
                      name
                    }
                  }
                }
                isAdult
                nextAiringEpisode {
                  id
                  airingAt
                  timeUntilAiring
                  episode
                  mediaId
                }
              }
            }
            """;

    public ResponseFetchByIdAniListDto fetchAnimeById(int id) {
        return executeQuery(FETCH_ANIME_BY_ID_QUERY, Map.of("id", id), ResponseFetchByIdAniListDto.class);
    }

    public ResponseFetchByIdAniListDto fetchAnimeByMalId(int id) {
        return executeQuery(FETCH_ANIME_BY_ID_QUERY, Map.of("idMal", id), ResponseFetchByIdAniListDto.class);
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

        return imageLink != null ? Map.of("link", imageLink) : null;
    }

    public ResponseTopSeasonAnimeDto fetchTopAnimeSeason() {
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
        return executeQuery(query, null, ResponseTopSeasonAnimeDto.class);
    }

    @Cacheable(value = "releasingAnimesCache", unless = "#result == null")
    public List<ResponseReleasingAnimesAniListDto.Data.Page.Media> fetchReleasingAnimes() {
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
        int maxPages = 3;
        List<ResponseReleasingAnimesAniListDto.Data.Page.Media> releasingAnimes = new ArrayList<>();
        do {
            ResponseReleasingAnimesAniListDto response = executeQuery(query, Map.of("page", nPage, "perPage", 50), ResponseReleasingAnimesAniListDto.class);

            if (response == null || response.data() == null) {
                return Collections.emptyList();
            }
            lastPage = response.data().page().pageInfo().lastPage();
            nPage++;
            releasingAnimes.addAll(response.data().page().media());
        }
        while (nPage <= lastPage && nPage <= maxPages);

        return releasingAnimes;
    }

    public ResponseAnimeCardAniListDto fetchUpcomingAnimeReleases(int cant) {
        final String query = String.format("""
                        query {
                          Page(perPage: %d) {
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
                
                """, cant);

        return executeQuery(query, null, ResponseAnimeCardAniListDto.class);
    }

    public ResponseAnimeCardAniListDto fetchSeasonTrendAnimes(int cant) {
        MediaSeason actualSeason = MediaSeason.current();
        int actualYear = LocalDate.now().getYear();
        final String query = String.format("""
                        query {
                          Page(perPage: %d) {
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
                
                """, cant, actualSeason.toString(), actualYear);

        return executeQuery(query, null, ResponseAnimeCardAniListDto.class);
    }

    public ResponseAnimeCardAniListDto fetchMostValoratedAnimes(int cant) {
        final String query = String.format("""
                        query {
                          Page(perPage: %d) {
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
                
                """, cant);

        return executeQuery(query, null, ResponseAnimeCardAniListDto.class);
    }

    @Cacheable(value = "tagsCache", unless = "#result == null")
    public ResponseTagsAniListDto fetchAllTags() {
        final String query = """
                        query {
                          MediaTagCollection {
                            name
                            description
                            isAdult
                          }
                        }
                """;

        return executeQuery(query, null, ResponseTagsAniListDto.class);
    }

    @Cacheable(value = "genreAnimeCache", unless = "#result == null")
    public ResponseGenresAniListDto fetchAllGenres() {
        final String query = """
                        query {
                          GenreCollection
                        }
                """;

        return executeQuery(query, null, ResponseGenresAniListDto.class);
    }

    @Cacheable(value = "filteredAnimesCache", key = "{#cant, #name, #tags, #genres, #year, #season, #status}", unless = "#result == null")
    public ResponseAnimeCardAniListDto fetchAnimesByFilters(int cant, String name, List<String> tags, List<String> genres, int year, String season, String status) {
        String query = """
                             query ($perPage: Int, $genre_in: [String], $tag_in: [String], $season: MediaSeason, $seasonYear: Int, $status: MediaStatus, $search: String) {
                               Page(perPage: $perPage) {
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
                """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("perPage", cant);

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

        return executeQuery(query, variables, ResponseAnimeCardAniListDto.class);
    }
}
