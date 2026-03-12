package com.graso.anitrack.user.infrastructure.adapter;

import com.graso.anitrack.external.jikan.JikanApiClient;
import com.graso.anitrack.user.application.port.out.UserQueryPort;
import com.graso.anitrack.user.domain.UserJikan.RandomUserJikan;
import com.graso.anitrack.user.domain.UserJikan.UserJikan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class JikanUserAdapter implements UserQueryPort {
    private final JikanApiClient jikanApiClient;

    @Override
    public List<RandomUserJikan> findRandomUsers(int count) {
        return jikanApiClient.fetchRandomUsers(count);
    }

    @Override
    public UserJikan findUserByUsername(String username) {
        return jikanApiClient.fetchUserByUsername(username);
    }
}



