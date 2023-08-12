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
 * @since 31/07/2023
 */

@Getter
@Setter
public class UpdateForm {

    @NotNull(message = "ID cannot be null or empty")
    private Long id;

    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email. Please enter a valid email address")
    private String email;

    private String address;

    //cela signifie que le numero contient exactement 8 chiffres
    @Pattern(regexp = "^\\d{8}$", message = "Invalid phone number.")
    private String phone;

    private String title;

    private String bio;
}
