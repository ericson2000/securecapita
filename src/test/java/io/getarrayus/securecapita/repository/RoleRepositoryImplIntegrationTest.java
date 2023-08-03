package io.getarrayus.securecapita.repository;

import io.getarrayus.securecapita.config.InfrastructureTestConfig;
import io.getarrayus.securecapita.domain.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
public class RoleRepositoryImplIntegrationTest {

    @Autowired
    private RoleRepository<Role> roleRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbc;


    @BeforeEach
    void setUpDatabase() {
        jdbc.execute("CREATE TABLE  Roles (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,name VARCHAR(50),permission VARCHAR(255),CONSTRAINT UQ_Roles_Name UNIQUE (name))", ps -> ps.execute());
    }

    @Test
    void given_role_when_create_then_role_is_persisted() {

        //GIVEN
        Role role = Role.builder().name("ROLE_USER").permission("READ:USER,READ:CUSTOMER").build();

        //WHEN
        Role persistedRole = roleRepository.create(role);

        //THEN
        Assertions.assertThat(persistedRole.getId()).isEqualTo(1L);
        Assertions.assertThat(persistedRole.getName()).isEqualTo(persistedRole.getName());
        Assertions.assertThat(persistedRole.getPermission()).isEqualTo(persistedRole.getPermission());

    }


    @BeforeAll
    void clearDatabase() {
        jdbc.execute("DROP TABLE IF EXISTS Roles", ps -> ps.execute());
    }

}
