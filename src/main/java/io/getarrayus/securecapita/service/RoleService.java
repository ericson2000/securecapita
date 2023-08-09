package io.getarrayus.securecapita.service;

import io.getarrayus.securecapita.domain.Role;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

public interface RoleService {
    Role getRoleByUserId(Long userId);
}
