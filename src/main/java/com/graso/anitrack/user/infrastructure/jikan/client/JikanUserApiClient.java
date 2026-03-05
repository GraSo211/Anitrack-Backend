package com.graso.anitrack.user.infrastructure.jikan.client;


import com.graso.anitrack.user.domain.RandomUserJikan;
import com.graso.anitrack.user.domain.UserJikan;
import com.graso.anitrack.user.infrastructure.jikan.dto.ResponseUserByIdJikanDto;
import com.graso.anitrack.user.infrastructure.jikan.dto.ResponseUsersJikanDto;
import com.graso.anitrack.user.infrastructure.mapper.JikanUserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class JikanUserApiClient {
    private WebClient.Builder webClientBuilder;
    private JikanUserMapper jikanUserMapper;


    public List<RandomUserJikan> fetchRandomUsers(int count) {
        String url = "https://api.jikan.moe/v4/users?limit=" + count;
        ResponseUsersJikanDto responseUsersJikanDto = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(ResponseUsersJikanDto.class)
                .block();

        List<RandomUserJikan> userJikanArrayList = new ArrayList<>();
        responseUsersJikanDto.data().stream().map(jikanUserMapper::toRandomUserJikan).forEach(userJikanArrayList::add);
        return userJikanArrayList;
    }

    public UserJikan fetchUserByUsername(String username) {
        String url = "https://api.jikan.moe/v4/users/" + username + "/full";
        ResponseUserByIdJikanDto user = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(ResponseUserByIdJikanDto.class)
                .block();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return jikanUserMapper.toUser(user);
    }
}
