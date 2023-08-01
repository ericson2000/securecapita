package io.getarrayus.securecapita.mapper;

import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToUserDto(User user);


    User userDtoToUser(UserDto userDto);
}
