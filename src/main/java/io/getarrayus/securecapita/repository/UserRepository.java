package io.getarrayus.securecapita.repository;

import io.getarrayus.securecapita.domain.User;

import java.util.Collection;

public interface UserRepository<T extends User> {
    /* Basic CRUD Operations */

    T create(T data);

    Collection<T> list(int page, int pagaSize);

    T get(Long id);

    T update(T data);

    Boolean delete(Long id);

    User getUserByEmail(String email);

    void sendVerificationCode(User user);

    User verifyCode(String email, String code);

    void resetPassword(String email);

    User verifyPasswordKey(String key);

    void renewPassword(String key, String password, String confirmPassword);

    /* More Complex Operations */


}
