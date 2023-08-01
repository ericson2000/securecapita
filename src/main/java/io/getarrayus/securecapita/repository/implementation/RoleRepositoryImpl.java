package io.getarrayus.securecapita.repository.implementation;

import io.getarrayus.securecapita.domain.Role;
import io.getarrayus.securecapita.exception.ApiException;
import io.getarrayus.securecapita.repository.RoleRepository;
import io.getarrayus.securecapita.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static io.getarrayus.securecapita.enums.RoleType.ROLE_USER;
import static io.getarrayus.securecapita.query.RoleQuery.INSERT_ROLE_TO_USER_QUERY;
import static io.getarrayus.securecapita.query.RoleQuery.SELECT_ROLE_BY_NAME_QUERY;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list(int page, int pagaSize) {
        return null;
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        log.info("Adding role {} to user id: {}", roleName, userId);

        try {
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("roleName", roleName), new RoleRowMapper());
            jdbc.update(INSERT_ROLE_TO_USER_QUERY, Map.of("userId", userId, "roleId", requireNonNull(role).getId()));

        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occured . Please try again");
        }
    }

    @Override
    public Role getRoleByUserId(Long userId) {
        return null;
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {

    }
}
