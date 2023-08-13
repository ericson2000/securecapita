package io.getarrayus.securecapita.service.implementation;

import io.getarrayus.securecapita.domain.Role;
import io.getarrayus.securecapita.repository.RoleRepository;
import io.getarrayus.securecapita.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository<Role> roleRepository;

    @Override
    public Role getRoleByUserId(Long userId) {
        return roleRepository.getRoleByUserId(userId);
    }

    @Override
    public Collection<Role> getRoles() {
        return roleRepository.getRoles();
    }
}
