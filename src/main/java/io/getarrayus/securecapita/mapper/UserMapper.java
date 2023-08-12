package io.getarrayus.securecapita.mapper;

import io.getarrayus.securecapita.domain.Role;
import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.dto.UserDto;
import io.getarrayus.securecapita.form.UpdateForm;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToUserDto(User user);


    @Mapping(source = "user", target = "roleName", qualifiedByName = "toRoleName")
    @Mapping(source = "user", target = "permissions", qualifiedByName = "toRolePermissions")
    UserDto userToUserDtoWithRole(User user, @Context Role role);


    User userDtoToUser(UserDto userDto);


    User updateFormToUser(UpdateForm updateForm);


    @Named("toRoleName")
    static String toRoleName(User user, @Context Role role) {
        return role.getName();
    }

    @Named("toRolePermissions")
    static String toRolePermissions(User user, @Context Role role) {
        return role.getPermission();
    }
}
