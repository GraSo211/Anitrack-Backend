package com.graso.anitrack.user.infrastructure.adapter;

import com.graso.anitrack.external.jikan.JikanApiClient;
import com.graso.anitrack.external.jikan.dto.ResponseUserByIdJikanDto;
import com.graso.anitrack.external.jikan.dto.ResponseUsersJikanDto;
import com.graso.anitrack.external.jikan.mapper.JikanUserMapper;
import com.graso.anitrack.user.application.port.out.JikanUserQueryPort;
import com.graso.anitrack.user.domain.userjikan.RandomUserJikan;
import com.graso.anitrack.user.domain.userjikan.UserJikan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class JikanUserAdapter implements JikanUserQueryPort {
    private final JikanApiClient jikanApiClient;
    private final JikanUserMapper jikanUserMapper;

    @Override
    public List<RandomUserJikan> findRandomUsers(int count) {
        ResponseUsersJikanDto response = jikanApiClient.fetchRandomUsers(count);
        return response.data().stream()
                .map(jikanUserMapper::toRandomUserJikan)
                .toList();
    }

    @Override
    public UserJikan findUserByUsername(String username) {
        ResponseUserByIdJikanDto response = jikanApiClient.fetchUserByUsername(username);
        return jikanUserMapper.toUser(response);
    }
}
