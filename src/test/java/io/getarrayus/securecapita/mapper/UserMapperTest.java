package io.getarrayus.securecapita.mapper;

import io.getarrayus.securecapita.domain.Role;
import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

public class UserMapperTest {

    @Test
    void given_user_map_userDto() {
        //GIVEN
        User user = User.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .address("address")
                .phone("phone")
                .title("title")
                .bio("bio")
                .imageUrl("imageUrl")
                .usingMfa(false)
                .locked(true)
                .enabled(true)
                .build();

        //WHEN
        final UserDto userDto = UserMapper.INSTANCE.userToUserDto(user);

        //THEN
        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(userDto.getLastName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(user.getAddress()).isEqualTo(userDto.getAddress());
        assertThat(user.getPhone()).isEqualTo(userDto.getPhone());
        assertThat(user.getTitle()).isEqualTo(userDto.getTitle());
        assertThat(user.getBio()).isEqualTo(userDto.getBio());
        assertThat(user.getImageUrl()).isEqualTo(userDto.getImageUrl());
        assertThat(user.isEnabled()).isEqualTo(userDto.isEnabled());
        assertThat(user.isUsingMfa()).isEqualTo(userDto.isUsingMfa());
        assertThat(user.isLocked()).isEqualTo(userDto.isLocked());
    }

    @Test
    void given_userDto_map_user() {
        //GIVEN
        UserDto userDto = UserDto.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .address("address")
                .phone("phone")
                .title("title")
                .bio("bio")
                .imageUrl("imageUrl")
                .usingMfa(false)
                .locked(true)
                .enabled(true)
                .build();

        //WHEN
        final User user = UserMapper.INSTANCE.userDtoToUser(userDto);

        //THEN
        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(userDto.getLastName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(user.getAddress()).isEqualTo(userDto.getAddress());
        assertThat(user.getPhone()).isEqualTo(userDto.getPhone());
        assertThat(user.getTitle()).isEqualTo(userDto.getTitle());
        assertThat(user.getBio()).isEqualTo(userDto.getBio());
        assertThat(user.getImageUrl()).isEqualTo(userDto.getImageUrl());
        assertThat(user.isEnabled()).isEqualTo(userDto.isEnabled());
        assertThat(user.isUsingMfa()).isEqualTo(userDto.isUsingMfa());
        assertThat(user.isLocked()).isEqualTo(userDto.isLocked());
    }

    @Test
    void given_user_map_UserDtoWithRole() {
        //GIVEN
        User user = User.builder()
                .build();
        Role role = Role.builder().name("ROLE_MANAGER").permission("READ:USER,READ:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER").build();

        //WHEN
        final UserDto userDto = UserMapper.INSTANCE.userToUserDtoWithRole(user, role);

        //THEN
        assertThat(userDto.getRoleName()).isEqualTo(role.getName());
        assertThat(userDto.getPermissions()).isEqualTo(role.getPermission());

    }
}
