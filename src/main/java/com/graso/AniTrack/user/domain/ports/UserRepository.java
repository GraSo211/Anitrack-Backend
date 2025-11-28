package com.graso.anitrack.user.domain.ports;

import java.util.Optional;

import com.graso.anitrack.user.domain.User;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User upsert(User user);
}
