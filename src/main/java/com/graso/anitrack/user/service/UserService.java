package com.graso.anitrack.user.service;

import com.graso.anitrack.user.client.myanimelist.MyAnimeListUserClient;
import com.graso.anitrack.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final MyAnimeListUserClient myAnimeListUserClient;

    public User getMyUser(String token) {
        return myAnimeListUserClient.getMyUser(token);
    }
}
