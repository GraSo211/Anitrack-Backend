package com.graso.anitrack.user.application.port.in;

import com.graso.anitrack.user.domain.UserJikan.UserJikan;

public interface GetUserByUsernameUseCase {
    UserJikan getUserByUsername(String username);
}
