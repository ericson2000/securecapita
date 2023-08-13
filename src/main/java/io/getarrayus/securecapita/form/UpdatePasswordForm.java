package io.getarrayus.securecapita.form;

import jakarta.validation.constraints.NotEmpty;
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
public class UpdatePasswordForm {

    @NotEmpty(message = "Current Password cannot be empty")
    private String currentPassword;

    @NotEmpty(message = "New Password cannot be empty")
    private String newPassword;

    @NotEmpty(message = "Confirm Password cannot be empty")
    private String confirmNewPassword;
}
