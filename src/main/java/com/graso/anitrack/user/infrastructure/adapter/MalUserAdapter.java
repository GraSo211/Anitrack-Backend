package com.graso.anitrack.user.infrastructure.adapter;

import com.graso.anitrack.external.myanimelist.MyAnimeListApiClient;
import com.graso.anitrack.external.myanimelist.dto.ResponseUserRequest;
import com.graso.anitrack.external.myanimelist.mapper.MyAnimeListUserMapper;
import com.graso.anitrack.user.application.port.out.MyAnimeListUserQueryPort;
import com.graso.anitrack.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MalUserAdapter implements MyAnimeListUserQueryPort {
    private final MyAnimeListApiClient myAnimeListApiClient;
    private final MyAnimeListUserMapper myAnimeListUserMapper;

    @Override
    public User findMyUser(String token) {
        ResponseUserRequest response = myAnimeListApiClient.getMyUser(token);
        return myAnimeListUserMapper.responseUserRequestToUser(response);
    }
}
