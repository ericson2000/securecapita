package io.getarrayus.securecapita.service.implementation;

import io.getarrayus.securecapita.domain.Role;
import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.dto.UserDto;
import io.getarrayus.securecapita.form.UpdateForm;
import io.getarrayus.securecapita.repository.RoleRepository;
import io.getarrayus.securecapita.repository.UserRepository;
import io.getarrayus.securecapita.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.getarrayus.securecapita.mapper.UserMapper.INSTANCE;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;

    @Override
    public UserDto createUser(User user) {
        return mapToUserDto(userRepository.create(user));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return mapToUserDto(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDto userDto) {
        userRepository.sendVerificationCode(INSTANCE.userDtoToUser(userDto));
    }

    @Override
    public UserDto verifyCode(String email, String code) {
        return mapToUserDto(userRepository.verifyCode(email, code));
    }

    @Override
    public void resetPassword(String email) {
        userRepository.resetPassword(email);
    }

    @Override
    public UserDto verifyPasswordKey(String key) {
        return mapToUserDto(userRepository.verifyPasswordKey(key));
    }

    @Override
    public void renewPassword(String key, String password, String confirmPassword) {
        userRepository.renewPassword(key, password, confirmPassword);
    }

    @Override
    public UserDto verifyAccountKey(String key) {
        return mapToUserDto(userRepository.verifyAccountKey(key));
    }

    @Override
    public UserDto updateUserDetails(UpdateForm user) {
        return mapToUserDto(userRepository.updateUserDetails(INSTANCE.updateFormToUser(user)));
    }

    @Override
    public UserDto getUserById(Long userId) {
        return mapToUserDto(userRepository.getUserById(userId));
    }

    private UserDto mapToUserDto(User user) {
        return INSTANCE.userToUserDtoWithRole(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
