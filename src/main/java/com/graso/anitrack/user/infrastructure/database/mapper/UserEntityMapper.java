package com.graso.anitrack.user.infrastructure.database.mapper;



import com.graso.anitrack.user.domain.User;
import com.graso.anitrack.user.infrastructure.database.entity.UserEntity;
import org.springframework.stereotype.Component;


@Component
public class UserEntityMapper {

    public UserEntity mapToUserEntity(User user){
        UserEntity userEntity = new UserEntity(
                user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getRole()
        );
        return  userEntity;
    }

    public User mapToUser(UserEntity userEntity){
        User user = User.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .role(userEntity.getRole())
                .build();
        return user;
    };
}