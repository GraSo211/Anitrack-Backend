package com.graso.anitrack.anime.infrastructure.anilist.client;


import com.graso.anitrack.anime.domain.model.Media;
import com.graso.anitrack.anime.infrastructure.anilist.dto.PostQueryDto;
import com.graso.anitrack.anime.infrastructure.anilist.dto.ResponseAniListDto;
import com.graso.anitrack.anime.infrastructure.mapper.AniListAnimeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@AllArgsConstructor
public class AniListApiClient {
    private WebClient.Builder webClientBuilder ;
    private AniListAnimeMapper aniListAnimeMapper;


    public Media fetchAnimeById(Long id){
        final String query=
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
        Mono<ResponseAniListDto> responseDtoMono = webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseAniListDto.class);
        ResponseAniListDto responseAniListDto = responseDtoMono.block();
        return aniListAnimeMapper.toDomain(responseAniListDto) ;
    }


}
