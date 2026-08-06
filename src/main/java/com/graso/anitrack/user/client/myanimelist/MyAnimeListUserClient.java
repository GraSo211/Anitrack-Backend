package com.graso.anitrack.user.client.myanimelist;

import com.graso.anitrack.user.client.myanimelist.MyAnimeListApiClient;
import com.graso.anitrack.user.client.myanimelist.mapper.MyAnimeListUserMapper;
import com.graso.anitrack.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MyAnimeListUserClient {
    private final MyAnimeListApiClient myAnimeListApiClient;
    private final MyAnimeListUserMapper myAnimeListUserMapper;

    public User getMyUser(String token) {
        var response = myAnimeListApiClient.getMyUser(token);
        return myAnimeListUserMapper.responseUserRequestToUser(response);
    }
}
