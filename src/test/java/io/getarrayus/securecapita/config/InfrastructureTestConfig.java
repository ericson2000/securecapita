package io.getarrayus.securecapita.config;

import io.getarrayus.securecapita.config.datasource.JpaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "io.getarrayus.securecapita.repository")
@ComponentScan({"io.getarrayus.securecapita.repository"})
@Import({JpaConfig.class})
@RequiredArgsConstructor
public class InfrastructureTestConfig {

    private static final int STRENGHT = 12;

    private final DataSource dataSource;

    @Bean
    public BCryptPasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder(STRENGHT);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
