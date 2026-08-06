package com.graso.anitrack.anime.client.anilist;

import com.graso.anitrack.anime.client.anilist.dto.*;
import com.graso.anitrack.anime.client.anilist.mapper.AniListAnimeMapper;
import com.graso.anitrack.anime.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class AniListClient {
    private final WebClient aniListWebClient;
    private final AniListAnimeMapper aniListAnimeMapper;

    @Value("${anitrack.anilist.releasing.sleep-ms:1500}")
    private long releasingSleepMs;

    @Value("${anitrack.anilist.releasing.max-pages:100}")
    private int releasingMaxPages;

    @Value("${anitrack.anilist.releasing.retry-after-429-ms:60000}")
    private long retryAfter429Ms;

    @Value("${anitrack.anilist.releasing.max-retries-per-page:3}")
    private int maxRetriesPerPage;

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

    public Anime findById(int id) {
        ResponseFetchByIdAniListDto response = executeQuery(FETCH_ANIME_BY_ID_QUERY, Map.of("id", id), ResponseFetchByIdAniListDto.class);
        return aniListAnimeMapper.toDomain(response);
    }

    public Anime findByMalId(int id) {
        ResponseFetchByIdAniListDto response = executeQuery(FETCH_ANIME_BY_ID_QUERY, Map.of("idMal", id), ResponseFetchByIdAniListDto.class);
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

        return imageLink != null ? Map.of("link", imageLink) : null;
    }

    public List<AnimeTopSeason> findTopSeasonAnime() {
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
            return List.of();
        }

        List<AnimeTopSeason> candidates = new ArrayList<>();
        ResponseTopSeasonAnimeDto.Data.AnimeData topScored = response.data().topScored();
        ResponseTopSeasonAnimeDto.Data.AnimeData topPopular = response.data().topPopular();

        if (topScored != null && topScored.media() != null) {
            topScored.media().stream()
                    .map(aniListAnimeMapper::toAnimeTopSeason)
                    .forEach(candidates::add);
        }
        if (topPopular != null && topPopular.media() != null) {
            topPopular.media().stream()
                    .map(aniListAnimeMapper::toAnimeTopSeason)
                    .forEach(candidates::add);
        }
        return candidates;
    }

    @Cacheable(value = "releasingAnimesCache", unless = "#result == null")
    public List<AnimeReleasing> findReleasingAnimes() {
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
        int maxPages = releasingMaxPages;
        List<AnimeReleasing> releasingAnimes = new ArrayList<>();
        boolean isFirstRequest = true;
        do {
            if (!isFirstRequest) {
                sleepBetweenRequests(releasingSleepMs);
            }
            isFirstRequest = false;

            ResponseReleasingAnimesAniListDto response;
            try {
                response = fetchReleasingPage(query, nPage);
            } catch (WebClientResponseException e) {
                log.warn("Giving up fetching releasing animes after {} retries on page {}. Returning {} animes accumulated so far",
                        maxRetriesPerPage, nPage, releasingAnimes.size(), e);
                break;
            }

            if (response == null || response.data() == null || response.data().page() == null) {
                log.warn("Stopping pagination of releasing animes: null data received on page {}", nPage);
                break;
            }
            lastPage = response.data().page().pageInfo().lastPage();
            nPage++;
            releasingAnimes.addAll(response.data().page().media().stream()
                    .map(aniListAnimeMapper::toAnimeReleasing)
                    .toList());
        }
        while (nPage <= lastPage && nPage <= maxPages);

        return releasingAnimes;
    }

    private ResponseReleasingAnimesAniListDto fetchReleasingPage(String query, int page) {
        int retries = 0;
        while (true) {
            try {
                return executeQuery(query, Map.of("page", page, "perPage", 50), ResponseReleasingAnimesAniListDto.class);
            } catch (WebClientResponseException e) {
                if (e.getStatusCode().value() == 429 && retries < maxRetriesPerPage) {
                    retries++;
                    long retryAfterMs = resolveRetryAfterMs(e);
                    log.warn("Rate limited by AniList on page {} (attempt {} of {}). Retrying after {} ms",
                            page, retries, maxRetriesPerPage, retryAfterMs);
                    sleepBetweenRequests(retryAfterMs);
                } else {
                    throw e;
                }
            }
        }
    }

    private long resolveRetryAfterMs(WebClientResponseException e) {
        String retryAfter = e.getHeaders().getFirst(HttpHeaders.RETRY_AFTER);
        if (retryAfter != null) {
            try {
                return Long.parseLong(retryAfter) * 1000;
            } catch (NumberFormatException ignored) {
                log.debug("Invalid Retry-After header value '{}', using default", retryAfter);
            }
        }
        return retryAfter429Ms;
    }

    private void sleepBetweenRequests(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted while waiting between AniList requests", e);
        }
    }

    public List<AnimeCard> findUpcomingAnimeReleases(int cant) {
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
        ResponseAnimeCardAniListDto response = executeQuery(query, null, ResponseAnimeCardAniListDto.class);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        return response.data()
                .page()
                .media()
                .stream()
                .map(aniListAnimeMapper::toAnimeCard)
                .toList();
    }

    public List<AnimeCard> findSeasonTrendAnimes(int cant) {
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
        ResponseAnimeCardAniListDto response = executeQuery(query, null, ResponseAnimeCardAniListDto.class);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        return response.data()
                .page()
                .media()
                .stream()
                .map(aniListAnimeMapper::toAnimeCard)
                .toList();
    }

    public List<AnimeCard> findMostValoratedAnimes(int cant) {
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
        ResponseAnimeCardAniListDto response = executeQuery(query, null, ResponseAnimeCardAniListDto.class);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        return response.data()
                .page()
                .media()
                .stream()
                .map(aniListAnimeMapper::toAnimeCard)
                .toList();
    }

    @Cacheable(value = "tagsCache", unless = "#result == null")
    public List<Tag> findAllTags() {
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
        return response.data()
                .mediaTagCollection()
                .stream()
                .map(aniListAnimeMapper::toTag)
                .toList();
    }

    @Cacheable(value = "genreAnimeCache", unless = "#result == null")
    public List<Genre> findAllGenres() {
        final String query = """
                        query {
                          GenreCollection
                        }
                """;
        ResponseGenresAniListDto response = executeQuery(query, null, ResponseGenresAniListDto.class);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        return response.data()
                .genreCollection()
                .stream()
                .map(aniListAnimeMapper::toGenre)
                .toList();
    }

    @Cacheable(value = "filteredAnimesCache", key = "{#cant, #name, #tags, #genres, #year, #season, #status}", unless = "#result == null")
    public List<AnimeCard> findAnimesByFilters(int cant, String name, List<String> tags, List<String> genres, int year, String season, String status) {
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

        ResponseAnimeCardAniListDto response = executeQuery(query, variables, ResponseAnimeCardAniListDto.class);

        if (response == null || response.data() == null) {
            return Collections.emptyList();
        }
        return response.data()
                .page()
                .media()
                .stream()
                .map(aniListAnimeMapper::toAnimeCard)
                .toList();
    }
}
