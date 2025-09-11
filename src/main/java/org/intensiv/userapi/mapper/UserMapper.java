package org.intensiv.userapi.mapper;

import org.intensiv.userapi.dto.request.CreateUserRequestDto;
import org.intensiv.userapi.dto.request.UpdateUserRequestDto;
import org.intensiv.userapi.dto.response.UserResponseDto;
import org.intensiv.userapi.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    User toUserEntity(CreateUserRequestDto createUserRequestDto);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UpdateUserRequestDto updateUserRequestDto, @MappingTarget User user);
}