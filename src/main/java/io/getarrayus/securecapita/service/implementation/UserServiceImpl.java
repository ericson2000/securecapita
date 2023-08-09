package io.getarrayus.securecapita.service.implementation;

import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.dto.UserDto;
import io.getarrayus.securecapita.mapper.UserMapper;
import io.getarrayus.securecapita.repository.UserRepository;
import io.getarrayus.securecapita.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userUserRepository;

    @Override
    public UserDto createUser(User user) {
        return UserMapper.INSTANCE.userToUserDto(userUserRepository.create(user));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return UserMapper.INSTANCE.userToUserDto(userUserRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDto userDto) {
        userUserRepository.sendVerificationCode(UserMapper.INSTANCE.userDtoToUser(userDto));
    }
}
