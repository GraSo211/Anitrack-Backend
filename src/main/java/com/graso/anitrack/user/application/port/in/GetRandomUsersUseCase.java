package com.graso.anitrack.user.application.port.in;

import com.graso.anitrack.user.domain.RandomUserJikan;

import java.util.List;

public interface GetRandomUsersUseCase {
    List<RandomUserJikan> getRandomUsers(int count);
}
