package com.graso.anitrack.user.application.port.out;

import com.graso.anitrack.user.domain.User;

public interface MyAnimeListUserQueryPort {
    User findMyUser(String token);
}
