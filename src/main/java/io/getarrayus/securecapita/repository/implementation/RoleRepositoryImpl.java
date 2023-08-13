package io.getarrayus.securecapita.repository.implementation;

import io.getarrayus.securecapita.domain.Role;
import io.getarrayus.securecapita.exception.ApiException;
import io.getarrayus.securecapita.repository.RoleRepository;
import io.getarrayus.securecapita.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static io.getarrayus.securecapita.enums.RoleType.ROLE_USER;
import static io.getarrayus.securecapita.query.RoleQuery.*;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {

    private static final String DEFAULT_ERROR_MESSAGE = "An error occured. Please try again";

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role role) {

        log.info("Adding role ");
        try {
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource parameters = getSqlParameterSource(role);
            jdbc.update(INSERT_ROLE_QUERY, parameters, holder);
            role.setId(requireNonNull(holder.getKey()).longValue());
            return role;

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
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
    public Role update(Role role) {
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
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public Role getRoleByUserId(Long userId) {
        log.info("Fetch role for user id: {}", userId);

        try {
            return jdbc.queryForObject(SELECT_ROLE_BY_ID_QUERY, Map.of("userId", userId), new RoleRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by id: " + userId);

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        log.info("Fetch role for email: {}", email);
        try {
            return jdbc.queryForObject(SELECT_ROLE_BY_EMAIL_QUERY, Map.of("email", email), new RoleRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by email: " + email);

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {

        log.info("Updating role for userId: {} and RoleName: {}", userId, roleName);
        try {
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("roleName", roleName), new RoleRowMapper());
            jdbc.update(UPDATE_USER_ROLE_QUERY, Map.of("roleId", role.getId(), "userId", userId));
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + roleName);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public Collection<Role> getRoles() {

        log.info("Fetchin all role in the database");

        try {
            return jdbc.query(SELECT_ROLES_QUERY, new RoleRowMapper());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    private SqlParameterSource getSqlParameterSource(Role role) {
        return new MapSqlParameterSource()
                .addValue("name", role.getName())
                .addValue("permission", role.getPermission());
    }
}
