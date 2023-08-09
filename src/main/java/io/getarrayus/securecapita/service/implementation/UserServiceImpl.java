package io.getarrayus.securecapita.service.implementation;

import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.dto.UserDto;
import io.getarrayus.securecapita.repository.UserRepository;
import io.getarrayus.securecapita.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.getarrayus.securecapita.mapper.UserMapper.INSTANCE;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;

    @Override
    public UserDto createUser(User user) {
        return INSTANCE.userToUserDto(userRepository.create(user));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return INSTANCE.userToUserDto(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDto userDto) {
        userRepository.sendVerificationCode(INSTANCE.userDtoToUser(userDto));
    }

    @Override
    public UserDto verifyCode(String email, String code) {
        return INSTANCE.userToUserDto(userRepository.verifyCode(email, code));
    }
}
