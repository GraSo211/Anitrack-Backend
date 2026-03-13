package com.graso.anitrack.user.application.port.in;

import com.graso.anitrack.user.domain.User;

public interface GetMyUserUseCase {
    User getMyUser(String token);
}
