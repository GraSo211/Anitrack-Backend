package com.graso.anitrack.user.application.service;

import com.graso.anitrack.user.application.port.in.GetRandomUsersUseCase;
import com.graso.anitrack.user.application.port.in.GetUserByUsernameUseCase;
import com.graso.anitrack.user.application.port.out.UserQueryPort;
import com.graso.anitrack.user.domain.RandomUserJikan;
import com.graso.anitrack.user.domain.UserJikan;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements GetRandomUsersUseCase, GetUserByUsernameUseCase {
    UserQueryPort userQueryPort;


    @Override
    public List<RandomUserJikan> getRandomUsers(int count) {
        return userQueryPort.findRandomUsers(count);
    }

    @Override
    public UserJikan getUserByUsername(String username) {
        return userQueryPort.findUserByUsername(username);
    }
}
