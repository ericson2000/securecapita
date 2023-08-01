package io.getarrayus.securecapita.config;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.datasource")
@Validated
public class DataSourceProperties {

    @NonNull
    private String url;

    @NonNull
    private String username;

    private String password;

    @NonNull
    private String driverClassName;

}
