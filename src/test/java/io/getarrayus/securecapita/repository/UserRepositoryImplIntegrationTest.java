package io.getarrayus.securecapita.repository;

import io.getarrayus.securecapita.config.InfrastructureTestConfig;
import io.getarrayus.securecapita.domain.Role;
import io.getarrayus.securecapita.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

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
@Disabled
public class UserRepositoryImplIntegrationTest {

    @Autowired
    private UserRepository<User> userUserRepository;

    @Autowired
    private RoleRepository<Role> roleRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;

    private Role role;

    @BeforeAll
    void setUp() {
        jdbc.execute("CREATE TABLE  Roles (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,name VARCHAR(50),permission VARCHAR(255),CONSTRAINT UQ_Roles_Name UNIQUE (name))", ps -> ps.execute());
        role = Role.builder().name("ROLE_USER").permission("READ:USER,READ:CUSTOMER").build();
        roleRepository.create(role);

        jdbc.execute("CREATE TABLE  Users (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,first_name VARCHAR(50),last_name VARCHAR(50),email VARCHAR(100),password VARCHAR(255) DEFAULT NULL,address VARCHAR(255) DEFAULT NULL,phone VARCHAR(30) DEFAULT NULL,title VARCHAR(50)  DEFAULT NULL,bio VARCHAR(255) DEFAULT NULL,enabled BOOLEAN DEFAULT FALSE,non_locked BOOLEAN DEFAULT TRUE,using_mfa  BOOLEAN      DEFAULT FALSE,image_url  VARCHAR(255) ,created_at DATETIME DEFAULT CURRENT_TIMESTAMP, CONSTRAINT UQ_Users_Email UNIQUE (email))", ps -> ps.execute());

        jdbc.execute("CREATE TABLE  UserRoles (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,user_id BIGINT NOT NULL,role_id BIGINT  NOT NULL,FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,FOREIGN KEY (role_id) REFERENCES Roles (id) ON DELETE RESTRICT ON UPDATE CASCADE,CONSTRAINT UQ_UserRoles_User_Id UNIQUE (user_id))", ps -> ps.execute());

        jdbc.execute("CREATE TABLE  AccountVerifications (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,user_id BIGINT NOT NULL,url VARCHAR(255) NOT NULL,FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE ON UPDATE CASCADE,CONSTRAINT UQ_AccountVerifications_User_Id UNIQUE (user_id),CONSTRAINT UQ_AccountVerifications_url UNIQUE (url))", ps -> ps.execute());

    }

    @Test
    void given_user_when_create_then_user_is_persisted() {
        //GIVEN
        User user = User.builder().firstName("firstName").lastName("lastName").email("email@gmail.com").password("123456").build();

        //WHEN
        User userPersisted = userUserRepository.create(user);

        //THEN
        Assertions.assertThat(userPersisted.getId()).isEqualTo(1L);
        Assertions.assertThat(userPersisted.getFirstName()).isEqualTo(userPersisted.getFirstName());
        Assertions.assertThat(userPersisted.getLastName()).isEqualTo(userPersisted.getLastName());
        Assertions.assertThat(userPersisted.getEmail()).isEqualTo(userPersisted.getEmail());

    }

    @AfterAll
    void clearDatabase() {
        jdbc.execute("DROP TABLE IF EXISTS AccountVerifications", ps -> ps.execute());
        jdbc.execute("DROP TABLE IF EXISTS UserRoles", ps -> ps.execute());
        jdbc.execute("DROP TABLE IF EXISTS Roles", ps -> ps.execute());
        jdbc.execute("DROP TABLE IF EXISTS Users", ps -> ps.execute());
    }
}
