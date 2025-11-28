package com.graso.anitrack.user.infrastructure.database;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.graso.anitrack.user.domain.User;
import com.graso.anitrack.user.domain.ports.UserRepository;
import com.graso.anitrack.user.infrastructure.database.entity.UserEntity;
import com.graso.anitrack.user.infrastructure.database.mapper.UserEntityMapper;
import com.graso.anitrack.user.infrastructure.database.repository.QueryUserRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final QueryUserRepository queryUserRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public Optional<User> findByEmail(String email) {
       return queryUserRepository.findByEmail(email).map(userEntityMapper::mapToUser);
    }

    @Override
    public boolean existsByEmail(String email) {
         return queryUserRepository.findByEmail(email).isPresent();
    }

    @Override
    public User upsert(User user) {
        UserEntity userEntity = userEntityMapper.mapToUserEntity(user);
        UserEntity savedEntity = queryUserRepository.save(userEntity);
        return userEntityMapper.mapToUser(savedEntity);
    }
    
}
