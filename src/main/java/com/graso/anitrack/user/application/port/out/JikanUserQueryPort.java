package com.graso.anitrack.user.application.port.out;

import com.graso.anitrack.user.domain.userjikan.RandomUserJikan;
import com.graso.anitrack.user.domain.userjikan.UserJikan;

import java.util.List;

public interface JikanUserQueryPort {
    List<RandomUserJikan> findRandomUsers(int count);

    UserJikan findUserByUsername(String username);


}
