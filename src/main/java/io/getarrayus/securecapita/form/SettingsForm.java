package io.getarrayus.securecapita.form;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@Getter
@Setter
public class SettingsForm {

    @NotNull(message = "Enabled cannot be null or empty")
    private Boolean enabled;
    @NotNull(message = "locked cannot be null or empty")
    private Boolean locked;
}
