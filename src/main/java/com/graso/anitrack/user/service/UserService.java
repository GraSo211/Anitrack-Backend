package com.graso.anitrack.user.service;

import com.graso.anitrack.user.client.jikan.JikanUserClient;
import com.graso.anitrack.user.client.myanimelist.MyAnimeListUserClient;
import com.graso.anitrack.user.model.RandomUserJikan;
import com.graso.anitrack.user.model.User;
import com.graso.anitrack.user.model.UserJikan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final JikanUserClient jikanUserClient;
    private final MyAnimeListUserClient myAnimeListUserClient;

    public List<RandomUserJikan> getRandomUsers(int count) {
        return jikanUserClient.findRandomUsers(count);
    }

    public UserJikan getUserByUsername(String username) {
        return jikanUserClient.findUserByUsername(username);
    }

    public User getMyUser(String token) {
        return myAnimeListUserClient.getMyUser(token);
    }
}
