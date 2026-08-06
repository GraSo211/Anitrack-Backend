package com.graso.anitrack.user.client.jikan;

import com.graso.anitrack.user.client.jikan.JikanApiClient;
import com.graso.anitrack.user.client.jikan.mapper.JikanUserMapper;
import com.graso.anitrack.user.model.RandomUserJikan;
import com.graso.anitrack.user.model.UserJikan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class JikanUserClient {
    private final JikanApiClient jikanApiClient;
    private final JikanUserMapper jikanUserMapper;

    public List<RandomUserJikan> findRandomUsers(int count) {
        var response = jikanApiClient.fetchRandomUsers(count);
        return response.data().stream()
                .map(jikanUserMapper::toRandomUserJikan)
                .toList();
    }

    public UserJikan findUserByUsername(String username) {
        var response = jikanApiClient.fetchUserByUsername(username);
        return jikanUserMapper.toUser(response);
    }
}
