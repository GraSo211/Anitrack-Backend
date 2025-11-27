package com.graso.anitrack.user.infrastructure.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.graso.anitrack.user.domain.User;
import com.graso.anitrack.user.infrastructure.database.entity.UserEntity;

@Mapper(componentModel=MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy=ReportingPolicy.ERROR)
public interface UserEntityMapper {

    @Mapping(target = "authorities", ignore = true)
    UserEntity mapToUserEntity(User user);
    
    User mapToUser(UserEntity userEntity);
}
