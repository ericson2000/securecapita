package io.getarrayus.securecapita.domain;

import io.getarrayus.securecapita.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static io.getarrayus.securecapita.mapper.UserMapper.INSTANCE;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {
    private final User user;
    private final Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return stream(this.role.getPermission().split(",".trim())).map(SimpleGrantedAuthority::new).collect(toList());
        return AuthorityUtils.commaSeparatedStringToAuthorityList(role.getPermission());
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isEnabled();
    }

    public UserDto getUser() {
        return INSTANCE.userToUserDtoWithRole(this.user, this.role);
    }

}
