package com.graso.anitrack.anime.infrastructure.anilist.client;


import com.graso.anitrack.anime.domain.model.Anime;
import com.graso.anitrack.anime.infrastructure.anilist.dto.PostQueryDto;
import com.graso.anitrack.anime.infrastructure.anilist.dto.ResponseAniListDto;
import com.graso.anitrack.anime.infrastructure.mapper.AniListAnimeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AniListApiClient {
    private WebClient.Builder webClientBuilder ;
    private AniListAnimeMapper aniListAnimeMapper;


    public Anime fetchAnimeById(Long id){
        final String query=
                """
                     query {
                           Media(id: ${id}) {
                             idMal
                             title {
                               romaji
                             }
                             coverImage {
                               extraLarge
                               large
                             }
                             description
                             bannerImage
                             episodes
                             startDate {
                               year
                               month
                               day
                             }
                             duration
                             isAdult
                             genres
                             averageScore
                             popularity
                             source
                             status
                             nextAiringEpisode {
                               airingAt
                               id
                               episode
                             }
                           }
                         }   
                """;
        PostQueryDto postQueryDto = new PostQueryDto(query, id);
        Mono<ResponseAniListDto> responseDtoMono =webClientBuilder.build()
                .post()
                .uri("https://graphql.anilist.co")
                .bodyValue(postQueryDto)
                .retrieve()
                .bodyToMono(ResponseAniListDto.class);
        System.out.println(responseDtoMono);
        ResponseAniListDto responseAniListDto = responseDtoMono.block();
        return aniListAnimeMapper.toDomain(responseAniListDto) ;
    }


}
