package io.getarrayus.securecapita.repository.implementation;

import io.getarrayus.securecapita.domain.Role;
import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.domain.UserPrincipal;
import io.getarrayus.securecapita.enums.VerificationType;
import io.getarrayus.securecapita.exception.ApiException;
import io.getarrayus.securecapita.repository.RoleRepository;
import io.getarrayus.securecapita.repository.UserRepository;
import io.getarrayus.securecapita.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

import static io.getarrayus.securecapita.enums.RoleType.ROLE_USER;
import static io.getarrayus.securecapita.enums.VerificationType.ACCOUNT;
import static io.getarrayus.securecapita.enums.VerificationType.PASSWORD;
import static io.getarrayus.securecapita.query.UserQuery.*;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateUtils.addDays;

@RequiredArgsConstructor
@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;
    private static final String USERS_COLUMN_ID = "ID";
    private static final String DEFAULT_ERROR_MESSAGE = "An error occured. Please try again";

    @Override
    public User create(User user) {
        // Check the email is unique
        if (isEmailExists(user.getEmail().trim().toLowerCase()))
            throw new ApiException("Email already in use. Please use a different email and try again.");
        // Save new user
        try {
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource parameters = getSqlParameterSource(user);
            jdbc.update(INSERT_USER_QUERY, parameters, holder);
//            user.setId(requireNonNull(holder.getKey()).longValue());
            user.setId((Long) requireNonNull(requireNonNull(holder.getKeys()).get(USERS_COLUMN_ID)));
            // Add role to the user
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
            // Send verification table
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
            // Save URL in verification table
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, Map.of("userId", user.getId(), "url", verificationUrl));
            // Send email to user with verification URL
//            emailService.sendVerificationUrl(user.getFirstName(),user.getEmail(),verificationUrl,ACCOUNT);
            user.setEnabled(false);
            user.setLocked(true);
            // Return the newly created user
            return user;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }


    @Override
    public Collection<User> list(int page, int pagaSize) {
        return null;
    }

    @Override
    public User getUserById(Long id) {
        try {
            return jdbc.queryForObject(SELECT_USER_BY_ID_QUERY, Map.of("id", id), new UserRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("No User found by id: " + id);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if (Objects.nonNull(user)) {
            log.info("User found in the database: {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()));
        } else {
            var message = "User not found in the database";
            log.error(message);
            throw new UsernameNotFoundException(message);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            return jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("No User found by email: " + email);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public void sendVerificationCode(User user) {
        String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
        String verificationCode = randomAlphabetic(8).toUpperCase();
        try {
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID_QUERY, Map.of("userId", user.getId()));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY, Map.of("userId", user.getId(), "code", verificationCode, "expirationDate", expirationDate));
//            sendSMS(user.getPhone(), "FROM: SecureCapita \nVerification code\n" + verificationCode);
            log.info("Verification Code: {}", verificationCode);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public User verifyCode(String email, String code) {
        if (isVerificationCodeExpired(code)) throw new ApiException("This code has expired. Please login again.");
        try {
            User userByCode = jdbc.queryForObject(SELECT_USER_BY_USER_CODE_QUERY, Map.of("code", code), new UserRowMapper());
            User userByEmail = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
            if (isEmailAreSame(userByCode, userByEmail)) {
                jdbc.update(DELETE_VERIFICATION_CODE_BY_CODE_QUERY, Map.of("code", code));
                return userByCode;
            } else {
                throw new ApiException("Code is invalid. Please try again.");
            }

        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("Could not find record");
        } catch (Exception exception) {
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public void resetPassword(String email) {
        if (!isEmailExists(email)) throw new ApiException("There is no account for this email address.");
        try {
            var expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
            User user = getUserByEmail(email);
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
            jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, Map.of("userId", user.getId()));
            jdbc.update(INSERT_PASSWORD_VERIFICATION_QUERY, Map.of("userId", user.getId(), "url", verificationUrl, "expirationDate", expirationDate));
            //TODO send email with url to user
            log.info("Verification URL: {}", verificationUrl);

        } catch (Exception exception) {
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public User verifyPasswordKey(String key) {
        if (isLinkExpired(key, PASSWORD))
            throw new ApiException("This link has expired. Please reset your password again.");

        try {
            return jdbc.queryForObject(SELECT_USER_BY_PASSWORD_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType())), new UserRowMapper());
            // jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, Map.of("userId", user.getId())); // Depends on use case / developer or business
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("This link is not valid. Please rest your password again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public void renewPassword(String key, String password, String confirmPassword) {
        if (!isPasswordAreSame(password, confirmPassword))
            throw new ApiException("Passwords don't match. Please try again.");

        try {
            jdbc.update(UPDATE_USER_PASSWORD_BY_URL_QUERY, Map.of("password", encoder.encode(password), "url", getVerificationUrl(key, PASSWORD.getType())));
            jdbc.update(DELETE_VERIFICATION_BY_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType())));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public User verifyAccountKey(String key) {
        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_ACCOUNT_QUERY, Map.of("url", getVerificationUrl(key, ACCOUNT.getType())), new UserRowMapper());
            jdbc.update(UPDATE_USER_ENABLED_QUERY, Map.of("enabled", true, "userId", user.getId()));
            return user;
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("This link is not valid.");
        } catch (Exception exception) {
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    @Override
    public User updateUserDetails(User user) {
        try {
            jdbc.update(UPDATE_USER_DETAILS_QUERY, getUserDetailsSqlParameterSource(user));
            return getUserById(user.getId());
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("No User found by id: " + user.getId());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    private Boolean isLinkExpired(String key, VerificationType password) {
        try {
            return jdbc.queryForObject(SELECT_EXPIRATION_DATE_BY_URL_QUERY, Map.of("url", getVerificationUrl(key, password.getType())), Boolean.class);
        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("This link is not valid. Please rest your password again.");
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    private boolean isEmailExists(String email) {
        Integer countEmail = jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
        return Objects.nonNull(countEmail) && countEmail > 0;
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }

    private SqlParameterSource getUserDetailsSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("address", user.getAddress())
                .addValue("title", user.getTitle())
                .addValue("bio", user.getBio())
                .addValue("phone", user.getPhone());
    }

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/verify/" + type + "/" + key).toUriString();
    }

    private boolean isEmailAreSame(User userByCode, User userByEmail) {
        return Objects.nonNull(userByCode)
                && Objects.nonNull(userByCode.getEmail())
                && Objects.nonNull(userByEmail)
                && userByCode.getEmail().equals(userByEmail.getEmail());
    }

    private Boolean isVerificationCodeExpired(String code) {
        try {
            return jdbc.queryForObject(SELECT_EXPIRATION_DATE_BY_CODE_QUERY, Map.of("code", code), Boolean.class);
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("This code is not valid. Please login again.");
        } catch (Exception exception) {
            throw new ApiException(DEFAULT_ERROR_MESSAGE);
        }
    }

    private boolean isPasswordAreSame(String password, String confirmPassword) {
        return Strings.isNotBlank(password) && password.equals(confirmPassword);
    }
}
