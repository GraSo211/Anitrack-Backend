package com.graso.anitrack.user.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.graso.anitrack.user.infrastructure.database.entity.UserEntity;


@Repository
public interface QueryUserRepository extends JpaRepository<UserEntity, Long> {
    
}
