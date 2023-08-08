package io.getarrayus.securecapita.repository;

import io.getarrayus.securecapita.config.InfrastructureTestConfig;
import io.getarrayus.securecapita.domain.Role;
import io.getarrayus.securecapita.domain.User;
import io.getarrayus.securecapita.exception.ApiException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(classes = {InfrastructureTestConfig.class})
@DirtiesContext
public class RoleRepositoryImplIntegrationTest {

    @Autowired
    private RoleRepository<Role> roleRepository;

    @Autowired
    private UserRepository<User> userUserRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    private Role role;


    @BeforeAll
    void setUpDatabase() {
        jdbc.execute("CREATE TABLE  Roles (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,name VARCHAR(50),permission VARCHAR(255),CONSTRAINT UQ_Roles_Name UNIQUE (name))", ps -> ps.execute());
        role = Role.builder().name("ROLE_USER").permission("READ:USER,READ:CUSTOMER").build();
        roleRepository.create(role);

        jdbc.execute("CREATE TABLE  Users (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,first_name VARCHAR(50),last_name VARCHAR(50),email VARCHAR(100),password VARCHAR(255) DEFAULT NULL,address VARCHAR(255) DEFAULT NULL,phone VARCHAR(30) DEFAULT NULL,title VARCHAR(50)  DEFAULT NULL,bio VARCHAR(255) DEFAULT NULL,enabled BOOLEAN DEFAULT FALSE,non_locked BOOLEAN DEFAULT TRUE,using_mfa  BOOLEAN      DEFAULT FALSE,image_url  VARCHAR(255) ,created_at DATETIME DEFAULT CURRENT_TIMESTAMP, CONSTRAINT UQ_Users_Email UNIQUE (email))", ps -> ps.execute());

        jdbc.execute("CREATE TABLE  UserRoles (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,user_id BIGINT NOT NULL,role_id BIGINT  NOT NULL,FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,FOREIGN KEY (role_id) REFERENCES Roles (id) ON DELETE RESTRICT ON UPDATE CASCADE,CONSTRAINT UQ_UserRoles_User_Id UNIQUE (user_id))", ps -> ps.execute());

        jdbc.execute("CREATE TABLE  AccountVerifications (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,user_id BIGINT NOT NULL,url VARCHAR(255) NOT NULL,FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,CONSTRAINT UQ_AccountVerifications_User_Id UNIQUE (user_id),CONSTRAINT UQ_AccountVerifications_url UNIQUE (url))", ps -> ps.execute());

    }

    @Test
    void given_role_when_create_then_role_is_persisted() {

        //GIVEN
        Role role = Role.builder().name("ROLE_MANAGER").permission("READ:USER,READ:CUSTOMER,UPDATE:USER,UPDATE:CUSTOMER").build();

        //WHEN
        Role persistedRole = roleRepository.create(role);

        //THEN
        assertThat(persistedRole.getId()).isNotNull();
        assertThat(persistedRole.getName()).isEqualTo(persistedRole.getName());
        assertThat(persistedRole.getPermission()).isEqualTo(persistedRole.getPermission());

    }

    @Test
    void given_user_persisted_when_getRoleByUserId_then_return_role_persisted() {
        //GIVEN
        User user = User.builder().firstName("firstName").lastName("lastName").email("email@gmail.com").password("123456").build();
        User userPersisted = userUserRepository.create(user);

        //WHEN
        Role rolePersisted = roleRepository.getRoleByUserId(userPersisted.getId());

        //THEN
        assertThat(rolePersisted).isNotNull();
        assertThat(rolePersisted.getName()).isEqualTo("ROLE_USER");
    }

    @Test
    void given_user_non_persisted_when_getRoleByUserId_then_throw_ApiException_with_specific_error_message() {
        //GIVEN
        User user = User.builder().firstName("firstName").lastName("lastName").email("email1@gmail.com").password("123456").build();
        userUserRepository.create(user);
        var wrongId = 209L;

        //WHEN
        Exception exception = Assertions.assertThrows(ApiException.class, () -> {
            roleRepository.getRoleByUserId(wrongId);
        });

        //THEN
        Assertions.assertEquals("No role found by id: " + wrongId, exception.getMessage());
    }

    @Test
    void given_user_persisted_when_getRoleByUserEmail_then_return_role_persisted() {
        //GIVEN
        User user = User.builder().firstName("firstName").lastName("lastName").email("email2@gmail.com").password("123456").build();
        User userPersisted = userUserRepository.create(user);

        //WHEN
        Role rolePersisted = roleRepository.getRoleByUserEmail(userPersisted.getEmail());

        //THEN
        assertThat(rolePersisted).isNotNull();
        assertThat(rolePersisted.getName()).isEqualTo("ROLE_USER");
    }

    @Test
    void given_user_non_persisted_when_getRoleByUserEmail_then_throw_ApiException_with_specific_error_message() {
        //GIVEN
        User user = User.builder().firstName("firstName").lastName("lastName").email("email3@gmail.com").password("123456").build();
        userUserRepository.create(user);
        var wrongEmail = "notexistemail@gmail.com";

        //WHEN
        Exception exception = Assertions.assertThrows(ApiException.class, () -> {
            roleRepository.getRoleByUserEmail(wrongEmail);
        });

        //THEN
        Assertions.assertEquals("No role found by email: " + wrongEmail, exception.getMessage());
    }

    @AfterAll
    void clearDatabase() {
        jdbc.execute("DROP TABLE IF EXISTS AccountVerifications", ps -> ps.execute());
        jdbc.execute("DROP TABLE IF EXISTS UserRoles", ps -> ps.execute());
        jdbc.execute("DROP TABLE IF EXISTS Roles", ps -> ps.execute());
        jdbc.execute("DROP TABLE IF EXISTS Users", ps -> ps.execute());
    }

}
