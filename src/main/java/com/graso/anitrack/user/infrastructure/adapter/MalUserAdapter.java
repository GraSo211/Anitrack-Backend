package com.graso.anitrack.user.infrastructure.adapter;

import com.graso.anitrack.external.myanimelist.MyAnimeListApiClient;
import com.graso.anitrack.user.application.port.out.MyAnimeListUserQueryPort;
import com.graso.anitrack.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MalUserAdapter implements MyAnimeListUserQueryPort {

    MyAnimeListApiClient myAnimeListApiClient;

    @Override
    public User findMyUser(String token) {
        return myAnimeListApiClient.getMyUser(token);
    }
}
