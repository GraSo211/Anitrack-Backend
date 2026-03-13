package com.graso.anitrack.user.application.service;

import com.graso.anitrack.user.application.port.in.GetMyUserUseCase;
import com.graso.anitrack.user.application.port.in.GetRandomUsersUseCase;
import com.graso.anitrack.user.application.port.in.GetUserByUsernameUseCase;
import com.graso.anitrack.user.application.port.out.JikanUserQueryPort;
import com.graso.anitrack.user.application.port.out.MyAnimeListUserQueryPort;
import com.graso.anitrack.user.domain.User;
import com.graso.anitrack.user.domain.UserJikan.RandomUserJikan;
import com.graso.anitrack.user.domain.UserJikan.UserJikan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements GetRandomUsersUseCase, GetUserByUsernameUseCase, GetMyUserUseCase {
    JikanUserQueryPort jikanUserQueryPort;
    MyAnimeListUserQueryPort myAnimeListUserQueryPort;


    @Override
    public List<RandomUserJikan> getRandomUsers(int count) {
        return jikanUserQueryPort.findRandomUsers(count);
    }

    @Override
    public UserJikan getUserByUsername(String username) {
        return jikanUserQueryPort.findUserByUsername(username);
    }


    @Override
    public User getMyUser(String token) {
        return myAnimeListUserQueryPort.findMyUser(token);
    }
}
