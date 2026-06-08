package com.graso.anitrack.user.application.service;

import com.graso.anitrack.user.application.port.out.JikanUserQueryPort;
import com.graso.anitrack.user.application.port.out.MyAnimeListUserQueryPort;
import com.graso.anitrack.user.domain.User;
import com.graso.anitrack.user.domain.userjikan.RandomUserJikan;
import com.graso.anitrack.user.domain.userjikan.UserJikan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final JikanUserQueryPort jikanUserQueryPort;
    private final MyAnimeListUserQueryPort myAnimeListUserQueryPort;

    public List<RandomUserJikan> getRandomUsers(int count) {
        return jikanUserQueryPort.findRandomUsers(count);
    }

    public UserJikan getUserByUsername(String username) {
        return jikanUserQueryPort.findUserByUsername(username);
    }

    public User getMyUser(String token) {
        return myAnimeListUserQueryPort.findMyUser(token);
    }
}
