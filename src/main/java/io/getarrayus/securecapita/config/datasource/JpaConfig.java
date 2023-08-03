package io.getarrayus.securecapita.config.datasource;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableConfigurationProperties({DataSourceProperties.class, JpaProperties.class})
@EnableJpaRepositories(basePackages = {"io.getarrayus.securecapita.config"})
@EnableTransactionManagement
@RequiredArgsConstructor
public class JpaConfig {

    private final DataSourceProperties dataSourceProperties;

    private final JpaProperties jpaProperties;

    @Bean
    @Primary
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSource = DataSourceBuilder.create();
        dataSource.driverClassName(dataSourceProperties.getDriverClassName());
        dataSource.url(dataSourceProperties.getUrl());
        dataSource.username(dataSourceProperties.getUsername());
        dataSource.password(dataSourceProperties.getPassword());
        return dataSource.build();
    }

    final Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();

        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", jpaProperties.getDdlAuto());
        hibernateProperties.setProperty("hibernate.dialect", jpaProperties.getDialect());
        hibernateProperties.setProperty("hibernate.show_sql", jpaProperties.getShowSql());
        hibernateProperties.setProperty("hibernate.generate_ddl", jpaProperties.getGenerateDdl());
        hibernateProperties.setProperty("hibernate.globally_quoted_identifiers", jpaProperties.getGloballyQuotedIdentifiers());

        return hibernateProperties;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setPackagesToScan("io.getarrayus.securecapita.domaine");
        em.setPersistenceUnitName("entityManagerFactory");
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;

    }


}
