package io.getarrayus.securecapita.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * @version 1.0
 * @Author Eric Wouwo Tionang
 * @licence
 */

@Getter
@Setter
 
 public class NewPasswordForm {

 @NotNull(message = "ID cannot be null or empty")
 private Long userId;

 @NotEmpty(message = "Password cannot be empty")
 private String password;

 @NotEmpty(message = "Confirm Password cannot be empty")
 private String confirmPassword;
}
