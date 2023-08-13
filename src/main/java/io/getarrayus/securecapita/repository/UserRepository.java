package io.getarrayus.securecapita.repository;

import io.getarrayus.securecapita.domain.User;

import java.util.Collection;

public interface UserRepository<T extends User> {
    /* Basic CRUD Operations */

    T create(T data);

    Collection<T> list(int page, int pagaSize);

    T getUserById(Long id);

    T update(T data);

    Boolean delete(Long id);

    User getUserByEmail(String email);

    void sendVerificationCode(User user);

    User verifyCode(String email, String code);

    void resetPassword(String email);

    User verifyPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);

    User verifyAccountKey(String key);

    User updateUserDetails(User user);

    void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword);

    /* More Complex Operations */


}
