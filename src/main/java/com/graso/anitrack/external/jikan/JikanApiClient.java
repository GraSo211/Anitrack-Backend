package com.graso.anitrack.external.jikan;

import com.graso.anitrack.anime.application.dto.EpisodePage;
import com.graso.anitrack.external.jikan.dto.ResponseEpisodesJikanDto;
import com.graso.anitrack.external.jikan.dto.ResponseUserByIdJikanDto;
import com.graso.anitrack.external.jikan.dto.ResponseUsersJikanDto;
import com.graso.anitrack.external.jikan.mapper.JikanAnimeMapper;
import com.graso.anitrack.external.jikan.mapper.JikanUserMapper;
import com.graso.anitrack.user.domain.UserJikan.RandomUserJikan;
import com.graso.anitrack.user.domain.UserJikan.UserJikan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class JikanApiClient {
    private WebClient jikanWebClient;
    private JikanAnimeMapper jikanAnimeMapper;
    private JikanUserMapper jikanUserMapper;


    public List<RandomUserJikan> fetchRandomUsers(int count) {
        ResponseUsersJikanDto response = jikanWebClient.get().uri("/users?limit={count}", count).retrieve().bodyToMono(ResponseUsersJikanDto.class).block();
        List<RandomUserJikan> userJikanArrayList = new ArrayList<>();
        response.data().stream().map(jikanUserMapper::toRandomUserJikan).forEach(userJikanArrayList::add);
        return userJikanArrayList;
    }

    public UserJikan fetchUserByUsername(String username) {
        String url = "https://api.jikan.moe/v4/users/" + username + "/full";
        ResponseUserByIdJikanDto response = jikanWebClient.get().uri("/users/{username}/full", username).retrieve().bodyToMono(ResponseUserByIdJikanDto.class).block();

        if (response == null) {
            throw new RuntimeException("User not found");
        }
        return jikanUserMapper.toUser(response);
    }

    public EpisodePage fetchAllEpisodesOfAnime(int animeId) {
        ResponseEpisodesJikanDto response = jikanWebClient.get().uri("/anime/{id}/episodes", animeId).retrieve().bodyToMono(ResponseEpisodesJikanDto.class).block();
        return jikanAnimeMapper.toEpisodePage(response);
    }


}
