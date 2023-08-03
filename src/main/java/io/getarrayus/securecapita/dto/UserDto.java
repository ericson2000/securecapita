package io.getarrayus.securecapita.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String address;

    private String phone;

    private String title;

    private String bio;

    private String imageUrl;

    private boolean enabled;

    private boolean locked;

    private boolean usingMfa;

    private LocalDateTime createdAt;
}
