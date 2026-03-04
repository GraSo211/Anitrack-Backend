package com.graso.anitrack.user.infrastructure.jikan.adapter;

import com.graso.anitrack.user.application.port.out.UserQueryPort;
import com.graso.anitrack.user.domain.RandomUserJikan;
import com.graso.anitrack.user.domain.UserJikan;
import com.graso.anitrack.user.infrastructure.jikan.client.JikanUserApiClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class JikanUserQueryAdapter implements UserQueryPort {
    private final JikanUserApiClient jikanApiClient;

    @Override
    public List<RandomUserJikan> findRandomUsers(int count) {
        return jikanApiClient.fetchRandomUsers(count);
    }

    @Override
    public UserJikan findUserByUsername(String username) {
        return jikanApiClient.fetchUserByUsername(username);
    }
}



