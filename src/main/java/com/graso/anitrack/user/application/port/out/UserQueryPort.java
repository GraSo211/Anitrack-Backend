package com.graso.anitrack.user.application.port.out;

import com.graso.anitrack.user.domain.UserJikan.RandomUserJikan;
import com.graso.anitrack.user.domain.UserJikan.UserJikan;

import java.util.List;

public interface UserQueryPort {
    List<RandomUserJikan> findRandomUsers(int count);

    UserJikan findUserByUsername(String username);

    
}
