package com.graso.anitrack.user.application.port.out;

import com.graso.anitrack.user.domain.RandomUserJikan;
import com.graso.anitrack.user.domain.UserJikan;

import java.util.List;

public interface UserQueryPort {
    List<RandomUserJikan> findRandomUsers(int count);

    UserJikan findUserByUsername(String username);
}
