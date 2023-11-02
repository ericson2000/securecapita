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
import org.springframework.web.multipart.MultipartFile;

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
    public void updatePassword(Long userId, String password, String confirmPassword) {
        userRepository.renewPassword(userId, password, confirmPassword);
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

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword) {
        userRepository.updatePassword(id, currentPassword, newPassword, confirmNewPassword);
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {
        roleRepository.updateUserRole(userId, roleName);
    }

    @Override
    public void updateAccountSettings(Long userId, boolean enabled, boolean notLocked) {
        userRepository.updateAccountSettings(userId, enabled, notLocked);
    }

    @Override
    public UserDto toggleMfa(String email) {
        return mapToUserDto(userRepository.toggleMfa(email));
    }

    @Override
    public void updateImage(UserDto userDto, MultipartFile image) {
        userRepository.updateImage(INSTANCE.userDtoToUser(userDto), image);
    }

    private UserDto mapToUserDto(User user) {
        return INSTANCE.userToUserDtoWithRole(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
