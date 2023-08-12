package io.getarrayus.securecapita.service;

import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.dto.UserDto;
import io.getarrayus.securecapita.form.UpdateForm;

public interface UserService {

    UserDto createUser(User user);

    UserDto getUserByEmail(String email);

    void sendVerificationCode(UserDto userDto);

    UserDto verifyCode(String email, String code);

    void resetPassword(String email);

    UserDto verifyPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);

    UserDto verifyAccountKey(String key);

    UserDto updateUserDetails(UpdateForm user);

    UserDto getUserById(Long userId);
}
