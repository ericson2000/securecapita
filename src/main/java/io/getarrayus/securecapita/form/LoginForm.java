package io.getarrayus.securecapita.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 * @since 31/07/2023
 */

@Data
public class LoginForm {

    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
