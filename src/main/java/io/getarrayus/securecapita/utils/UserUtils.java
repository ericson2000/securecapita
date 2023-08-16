package io.getarrayus.securecapita.utils;

import io.getarrayus.securecapita.domain.UserPrincipal;
import io.getarrayus.securecapita.dto.UserDto;
import org.springframework.security.core.Authentication;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

public class UserUtils {

    public static UserDto getAuthenticatedUser(Authentication authentication) {
        return authentication.getPrincipal() instanceof UserDto ?
                (UserDto) authentication.getPrincipal() : ((UserPrincipal) authentication.getPrincipal()).getUser();
    }
}
