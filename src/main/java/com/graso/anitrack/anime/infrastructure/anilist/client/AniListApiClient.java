package com.graso.anitrack.anime.infrastructure.anilist.client;


import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.domain.model.MediaSeason;
import com.graso.anitrack.anime.infrastructure.anilist.dto.PostQueryDto;
import com.graso.anitrack.anime.infrastructure.anilist.dto.ResponseBannerImageAniListDto;
import com.graso.anitrack.anime.infrastructure.anilist.dto.ResponseFetchByIdAniListDto;
import com.graso.anitrack.anime.infrastructure.mapper.AniListAnimeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Component
@AllArgsConstructor
public class AniListApiClient {
    private WebClient.Builder webClientBuilder;
    private AniListAnimeMapper aniListAnimeMapper;


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
                             description
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
}
