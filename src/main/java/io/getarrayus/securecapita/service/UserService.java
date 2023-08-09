package io.getarrayus.securecapita.service;

import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.dto.UserDto;

public interface UserService {

    UserDto createUser(User user);

    UserDto getUserByEmail(String email);

    void sendVerificationCode(UserDto userDto);
}
